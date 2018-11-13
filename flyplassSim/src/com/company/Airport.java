package com.company;
import jsjf.CircularArrayQueue;
import jsjf.EmptyCollectionException;
import java.sql.SQLOutput;
import java.util.Random;
import java.util.Scanner;

public class Airport {
    private static class Plane {
        private int arrival;
        private int id;
	
        public Plane(int arrival, int id){
            this.arrival = arrival;
            this.id = id;
        }
        public int timeToWait(int time){
        return time - arrival;
        }
	    
        public int getId() {
            return id;
        }
    }
    public static class runway {
        private int timeOnRunway;
        public runway(){
            timeOnRunway = 0;
        }
        public boolean runwayEmpty(int time){
            return time >= timeOnRunway;
        }
        public void setTimeOnRunway(int time){
            timeOnRunway = time;
        }
    }

    public static void simulate(int tU, int qZ, float pA, float pD ){
        runway runway = new runway();
        Random R = new Random();

        int totRunwayTime = 0;
        int totWaitTimeAir = 0;
        int totWaitTimeGround = 0;
        int totPlanesManaged = 0;
        int totLandReqDeclines = 0;
        int totTakeOffReqDeclines = 0;
        int totPlanesLanded = 0;
        int totPlaneTakeoffs = 0;
        int totTimeEmptyRunway = 0;

        CircularArrayQueue<Plane> airQ = new CircularArrayQueue<Plane>();
        CircularArrayQueue<Plane> groundQ = new CircularArrayQueue<Plane>();

        for(int time = 0; time < tU; time++){
            int counter = time + 1;
            System.out.println("Time unit " + counter + ": ");
            int planeArrivals = getPoissonRandom(pA);
            int planeDepature = getPoissonRandom(pD);

            for(int i = 0; i < planeArrivals; i++){
                int planeId = airQ.size() + groundQ.size() + totPlanesLanded + totPlaneTakeoffs + 1;
                //Landing
                if(airQ.size() < qZ){
                    Plane p = new Plane(time, planeId);
                    airQ.enqueue(p);
                    System.out.println("Plane "+ p.getId() + " arrived for landing.");
                } 
		else {
                    totLandReqDeclines++;
                    System.out.println("Plane " + planeId + " was declined landing.");
                }
                totPlanesManaged++;
            }

            for(int i = 0; i < planeDepature; i++){
            //takeoff
                int planeId = groundQ.size() + airQ.size() + totPlanesLanded + totPlaneTakeoffs + 1;
                if(groundQ.size() < qZ){
                    Plane p = new Plane(time, planeId);
                    groundQ.enqueue(p);
                    System.out.println("Plane "+ p.getId() + " is ready for takeoff.");
                } 
		else{
                    System.out.println("Plane "+ planeId + " was denied takeoff.");
                    totTakeOffReqDeclines++;
                }
                totPlanesManaged++;
            }
            // land or takeoff
            if(runway.runwayEmpty(time)){
                if(!airQ.isEmpty()){
                    Plane p = null;
                    try {
                        p = airQ.dequeue();
                        totPlanesLanded++;
                        System.out.println("Plane " + p.getId() + " landed.");
                    } 
		    catch (EmptyCollectionException e) {
                        e.printStackTrace();
                    }
                    totRunwayTime ++;
                    totWaitTimeAir += p.timeToWait(time);
                } 
		else if(!groundQ.isEmpty()){
                    Plane p = null;
                    try {
                        p = groundQ.dequeue();
                        totPlaneTakeoffs++;
                        System.out.println("Plane " + p.getId() + " departed.");
                    } 
		    catch (EmptyCollectionException e) {
                        e.printStackTrace();
                    }
                    totRunwayTime ++;
                    totWaitTimeGround  += p.timeToWait(time);
                } 
		else{
                    totTimeEmptyRunway++;
                    System.out.println("No arrivals/departures");
                }
            }
            System.out.println("Planes awaiting landing clearance: " + airQ.size());
            System.out.println("Planes awaiting takeoff clearance: " + groundQ.size());
            System.out.println("Average wait time for landing: " + (float)totWaitTimeAir/(float)totPlanesLanded);
            System.out.println("Average wait time for takeoff: " + (float)totWaitTimeGround/(float)totPlaneTakeoffs);
            System.out.println(" ");
        }
        int runwayUsage = totPlanesLanded + totPlaneTakeoffs;
        int runwayDowntime = tU - runwayUsage;
        System.out.println("Simulation finished after " + tU + " time units");
        System.out.println("Total planes managed         : " + totPlanesManaged);
        System.out.println("Total planes landed          : " + totPlanesLanded);
        System.out.println("Total planes departed        : " + totPlaneTakeoffs);
        System.out.println("Total planes rejected landing: " + totLandReqDeclines);
        System.out.println("Total planes awaiting landing: " + airQ.size());
        System.out.println("Total planes awating takeoff : " + groundQ.size());
        System.out.println("Percentage empty runway time : " + (runwayDowntime * 100 / tU));
        System.out.println("Average wait time for landing: " + (float)totWaitTimeAir/(float)totPlanesLanded);
        System.out.println("Average wait time for takeoff: " + (float)totWaitTimeGround/(float)totPlaneTakeoffs);
        System.out.println("Total planes denied takeoff  : " + totTakeOffReqDeclines);
    }
	
    public static void main(String[] args) {

	int timeUnits, maxTimeOnRunway, queueSize;
	float planeArr;
	float planeDep;
	    
        System.out.println("Velkommen til Halden Airport, tax-free butikken er dessverre stengt");
        Scanner sc = new Scanner(System.in);
        System.out.println("Select number of rounds the sim should run");
        timeUnits = sc.nextInt();
        System.out.println("set max queue size");
        queueSize = sc.nextInt();

        System.out.println("set base average plane arrival");
        planeArr = sc.nextFloat();
        System.out.println("Set base avrage plane depature");
        planeDep = sc.nextFloat();
        simulate(timeUnits, queueSize, planeArr, planeDep);
    }
    private static int getPoissonRandom(double mean)
    {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do
        {
            p = p * r.nextDouble();
            k++;
        }
        while (p > L);
        return k - 1;
    }
}
