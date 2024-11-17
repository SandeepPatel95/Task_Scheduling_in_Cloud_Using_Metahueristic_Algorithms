package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import utils.Constants;
import java.util.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap; 
import java.util.Map; 

public class EnhanceHarmonySearchDatacenterBroker extends DatacenterBroker {
	
	public static int HMS =20,missing;
	public static int no_of_Cloudlets;
	public static int[][]  init_Harmony = new int[20][30];
	public static int[][]  hybrid_Harmony = new int[20][30];
	public static int[]  newHarmony = new int[30];
	public static int[] newHarmonyhyb = new int[30];
	public static int[][] best_Five_Harmony = new int[5][30];
	public static HashMap<Integer, Cloudlet> map  = new HashMap<>();
	public static TreeMap<Double, ArrayList<Cloudlet>> sorted = new TreeMap<>(); 
	public static HashMap<Double, ArrayList<Cloudlet>> msMAP  = new HashMap<>();
	public int[] best_harmony = new int[30];
	public static int best_index, worst_Harmony, best_index_Hyb,worst_Harmony_Hyb;
	public static List<Cloudlet> list2 = new ArrayList<>();
	public static Double[] list4  = new Double[20];
	public static Double[] list4Hyb  = new Double[20];
	public static ArrayList<Double>BestCost = new ArrayList<>();
	int[] best = new int[30];
	public static boolean done1 = false,done = false,HMCApp = false,HMCHybApp = false, PAApp = false , PAHybApp = false;
	ArrayList<Double> makespanlist = new ArrayList<Double>();
    ArrayList<Cloudlet> list5 = new ArrayList<Cloudlet>();
    EnhanceHarmonySearchDatacenterBroker(String name) throws Exception {
        super(name);
    }

    public void scheduleTaskstoVms() {
        int reqTasks = cloudletList.size();
        int reqVms = vmList.size();
        Vm vm = vmList.get(0);

        for (int i = 0; i < reqTasks; i++) {
            bindCloudletToVm(i, (i % reqVms));
            System.out.println("Task" + cloudletList.get(i).getCloudletId() + " is bound with VM" + vmList.get(i % reqVms).getId());
        }

        //System.out.println("reqTasks: "+ reqTasks);

        ArrayList<Cloudlet> list = new ArrayList<Cloudlet>();
        for (Cloudlet cloudlet : getCloudletReceivedList()) {
            list.add(cloudlet);
        }

        //setCloudletReceivedList(null);

        list2 = list;

        ArrayList<Double> harmony = new ArrayList<Double>();
        list4 = harmony.toArray(new Double[20]);
        list4Hyb = harmony.toArray(new Double[20]);
        
        int n = list.size();
        no_of_Cloudlets = n;
        System.out.println(" Number of Cloudlets :" + no_of_Cloudlets );
        
        createInitHarmony();
        for(int i = 0; i < 20; i++ )
        {
        	for(int j = 0; j < 30; j++ )
            {
            hybrid_Harmony[i][j] = init_Harmony[i][j];
            }
        }
       
        
        for(int i=1;i<=list2.size();i++)
        {
        map.put(i, list2.get(i-1));
        }
        /*
        System.out.println(" The list 2 is: \n" );
        for(int i=1;i<= list2.size();i++)
        {
    	System.out.println(" " + map.get(i) );
        }
        */
        for (int i = 0; i < n; i++)
        {
        	//list4[i] = list2[i].getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
        	//list4[i] = SJF_Scheduler.calcMakespan(list2);
        }
        
        
        	System.out.println("Cost(MakeSpan) of Each Schedule is:");
        	 for(int i = 0; i < HMS; i++)
             {
             	list5.clear();
             	for(int j = 0; j < no_of_Cloudlets; j++)
             	{
             		list5.add(map.get(init_Harmony[i][j]));
             	}
             
             	double makespan = EnhanceHarmonySearchScheduler.calcMakespan(list5);
             	
             	 System.out.println(" The MAKESPAN of schedule " + i +   "is: " + makespan + "\n");
             	makespanlist.add(makespan);
             	msMAP.put(makespan, list5);
             	list4[i] = makespan;
             	list4Hyb[i] = makespan;
             }
        	 Collections.sort(makespanlist);
        	 System.out.println(makespanlist);
        	
        	
        	
        	
        	//list4[i] = list2[i].getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
        
        
        
       
        
        
        
        
        
        
        //harmonySearch()	 
       	 int nVar=30;       
    	 int[] VarSize=new int[nVar]; 
    	 int VarMin=1;         //Decision Variables Lower Bound
    	 int VarMax= 30;         //Decision Variables Upper Bound

    	 //Harmony Search Parameters
    	int MaxIt=20;    //Maximum Number of Iterations
    	int nNew=20;        //Number of New Harmonies
    	double HMCR=0.5,HMCRmax = 0.9,HMCRmin = 0.1,HMCRhyb = 0.9;       //Harmony Memory Consideration Rate
    	double PAR=0.3,PARhyb=0.9, PARmax = 0.9,PARmin = 0.1 ;       //Pitch Adjustment Rate
    	double FW=(VarMax-VarMin),FWhyb = FW, FWmax = FW, FWmin = FW / 5;    // Fret Width (Bandwidth)
    	
    	double FW_damp = 0.995;             // Fret Width Damp Ratio
    	double delta = 0.0,cost = 0.0,cost_Hyb = 0.0;
    	int pos = 0,PitchAdjEle;
    	 // Initialization

    	 // Empty Harmony Structure
    	// double[] empty_harmony_Position=new double[nVar];
    	 //double[] empty_harmony_Cost=new double[nVar];

    	 // Initialize Harmony Memory
    	// init_Harmony = repmat(empty_harmony,HMS,1);

    	 // Create Initial Harmonies
    	 //for(int i=1;i<=HMS;i++)
    	//	 init_Harmony(i).Position=unifrnd(VarMin,VarMax,VarSize);
    	 //init_Harmony(i).Cost=CostFunction(HM(i).Position);
    	 //end
    	 //Sort Harmony Memory
    	 //[~, SortOrder]=sort([init_Harmony.Cost]);
    	// init_Harmony=init_Harmony(SortOrder);

    	 // Update Best Solution Ever Found
    	// BestSol=HM(1); 

	
        best_index = getSmallest(list4);
        worst_Harmony = getLargest(list4);
        System.out.println("Best harmony using Basic: " + list4[best_index]);
        System.out.println("Worst harmony using Basic: " + list4[worst_Harmony]);
        best_index_Hyb = getSmallest(list4Hyb);
        worst_Harmony_Hyb = getLargest(list4Hyb);
        System.out.println("Best harmony using Hybrid: " + list4Hyb[best_index_Hyb]);
        System.out.println("Worst harmony using Hybrid: " + list4Hyb[worst_Harmony_Hyb]);
    	 
        
        
        
        
        
    	 // Harmony Search Main Loop
    	 for(int it=1;it <= MaxIt;it++)
    	     {
    		 
    		 	best_index = getSmallest(list4);
    		 	worst_Harmony = getLargest(list4);
    		 	best_index_Hyb = getSmallest(list4Hyb);
    		 	worst_Harmony_Hyb = getLargest(list4Hyb);
    	     // Initialize Array for New Harmonies
    	     //NEW=repmat(empty_harmony,nNew,1);
    		 	
    		//Finding Top Five Harmonies
    		 	
    		 	
    	        // Copy all data from hashMap into TreeMap 
    	        sorted.putAll(msMAP); 
    	        
    	        int count = 0;
    	        for (Map.Entry<Double,ArrayList<Cloudlet>> entry:sorted.entrySet()) 
    	        {
    	           if (count >= 5) break;

    	           for( int i = 0; i< 30; i++) 
    	           {
    	        	     
    	        	   for (Map.Entry<Integer,Cloudlet> entry2 : map.entrySet()) {
    	    	            if (entry2.getValue().equals(entry.getValue().get(i))) {
    	    	            	best_Five_Harmony[count][i] = entry2.getKey();
    	    	            	
    	    	            }
    	        	   }
    	        	   
    	        	   
    	        	  // map.containsKey(map.get(entry.getValue().get(i)));
    	        	
    	          // System.out.println(entry.getValue().get(i));
    	           }
    	           count++;
    	        }
    	        
    	        
    	        
    	        
    	        
    	        
    	     // Create New Harmony
    	     	
    	        int temp=(int)((Math.random()*20));
    	        for(int k=0; k < no_of_Cloudlets; k++)
    	        {
    	        newHarmony[k] = init_Harmony[temp][k];
    	        }
    	        temp=(int)((Math.random()*4));
    	        for(int k=0; k < no_of_Cloudlets; k++)
    	        {
    	        newHarmonyhyb[k] = hybrid_Harmony[temp][k];
    	        }
    	      /* for(int i = 0; i < no_of_Cloudlets; i++)
    	    	newHarmonyhyb[i] = v2.get(i);
    	       */
    	        
    	         // Create New Harmony Position
    	         //NEW(k).Position=unifrnd(VarMin,VarMax,VarSize);
    	        HMCRhyb = HMCRmax - ((HMCRmax - HMCRmin)* it)/ MaxIt;
    	        PARhyb = PARmax - ((PARmax - PARmin)* it)/ MaxIt;
    	        FWhyb = FWmax - ((FWmax - FWmin)* it)/ MaxIt;
    	        double raw;
    	         for(int j=0; j < nVar;j++)
    	         {
    	        	 //Harmony Consideration Basic
    	        	 while(j<30)
      	            {
    	        		 System.out.println("in While");
    	                  if((raw = Math.random())<=HMCR)
    	             {
    	                 // Use Harmony Memory
    	                pos=(int)((Math.random()*20));
    	                System.out.println("Before HMCR *** " + newHarmony[j] );
    	               //Check if element at init_Harmony[pos][j] is already in newHarmony or not;
    	               //CODE HERE CODE HERE System.out.println(alreadyInNewHarmony(newHarmony,init_Harmony[pos][j]));
    	                if(!alreadyInNewHarmony(newHarmony,init_Harmony[pos][j],j))
    	                {
    	               System.out.println("HMCR Applied*** " );
    	               newHarmony[j] = (int)(init_Harmony[pos][j]);
    	               //System.out.println(j +"HMCR ***" +  newHarmony[j] );
    	               HMCApp = true;
    	               System.out.println("After HMCR *** " + newHarmony[j] );
    	               break;
    	                }
    	               
    	             }
    	           //  System.out.println("Random and HMCR ******************"+r + " " + HMCR );
    	            
    	             
    	           
        	           //  System.out.println("Random and HMCRhyb ******************"+r + " " + HMCRhyb );
        	             // Pitch Adjustment
    	             if((raw = Math.random())<=PAR )
        	             {
    	            	 System.out.println("Before PA *** " + newHarmony[j] );
        	               //  delta=FW*((Math.random()*2)-1 )  ;   //Uniform
        	                 delta=FW*Math.random();           // Gaussian (Normal) 
        	                 PitchAdjEle = init_Harmony[pos][j] + (int)delta;
        	                 PitchAdjEle = PitchAdjEle % 31;
        	               //Check if  the new generated element is <=30 &>=1 and already in newHarmony
          	               //CODE HERE CODE HERE
        	                 if(PitchAdjEle<=30 && PitchAdjEle > 0 && !alreadyInNewHarmony(newHarmony,PitchAdjEle,j))
        	                 {
        	                	 System.out.println("PA Applied*** " );
        	                	 newHarmony[j]  = PitchAdjEle;
              	              //  System.out.println(j +"PA ***" +  newHarmony[j] );
              	              done1 = true;
              	            System.out.println("After PA*** " + newHarmony[j] );
              	              break;
        	                 }
        	                 
        	             }   
    	               
    	         	
   	            
    	         	System.out.println("New Harmony");
    	         	for(int i = 0;i <=j;i++ )
    	         	{
    	         		System.out.print(" " + newHarmony[i]);
    	         	}
    	         	System.out.println("\n");
    	         	
    	         	if(j == 27)
    	         	{
    	         	missing	= findMissing27(newHarmony);
    	         	newHarmony[27] = missing;	
    	         	j++;
    	         	break;
    	         	}
    	         	if(j == 28)
    	         	{
    	         	missing	= findMissing28(newHarmony);
    	         	newHarmony[28] = missing;	
    	         	j++;
    	         	break;
    	         	}
    	         	if(j == 29)
    	         	{
    	         	missing	= findMissing(newHarmony);
    	         	newHarmony[29] = missing;	
    	         	j++;
    	         	break;
    	         	}
    	         	
   	            	}
     	        
    	           
    	        	 
    	             
    	            
        	        //Harmony Consideration Hybrid
    	        
    	        	 while(j<30)
         	            {
    	        		 System.out.println("In Hybrid While");
         	            	pos=(int)((Math.random()*20));
        	             if((raw = Math.random()) <=HMCR)
    	             
        	             {
        	            	 System.out.println("Before Hybrid HMCR *** " + newHarmonyhyb[j] );
        	                 // Use Harmony Memory
        	                pos =(int)(pos/4);
        	               //Check if element at init_Harmony[pos][j] is already in newHarmony or not;
        	               //CODE HERE CODE HERE
        	                System.out.println("After Hybrid HMCR *** " + hybrid_Harmony[pos][j] );
        	                if(!alreadyInNewHarmony(newHarmonyhyb,hybrid_Harmony[pos][j],j))
        	                {
        	                	newHarmonyhyb[j] = (int)(hybrid_Harmony[pos][j]);
        	                	// System.out.println(j +"HMCR Hybrid Applied***" +  newHarmonyhyb[j] );
        	                	 HMCHybApp =true;
        	                	 done = true;
        	                	 
        	                	 break;
        	                	//System.out.println("Applied HMCR**************"+newHarmonyhyb[j] );
        	                }
        	              
        	               
        	                }
    	             
    	             // HYBRID Pitch Adjustment 
        	          
        	             
    	            if( (raw = Math.random()) <= PAR )
    	             {
    	            	 System.out.println("Before Hybrid PA *** " + newHarmonyhyb[j] );
    	            	 pos =(int)(pos/4);
    	               //  delta=FW*((Math.random()*2)-1 )  ;   //Uniform
    	                 delta=FWhyb*Math.random();           // Gaussian (Normal)
    	                
    	                 PitchAdjEle = hybrid_Harmony[pos][j] + (int)delta;
    	               //Check if  the new generated element is <=30 &>=1 and already in newHarmony
      	               //CODE HERE CODE HERE
    	                
    	                 PitchAdjEle = PitchAdjEle % 31;
    	                 System.out.println("After Hybrid PA*** " + PitchAdjEle );
    	                 if(PitchAdjEle<=30 && PitchAdjEle > 0 && !alreadyInNewHarmony(newHarmonyhyb,PitchAdjEle,j))
    	                 {
    	                	 newHarmonyhyb[j]  = PitchAdjEle;
    	                	 done = true;
    	                	 
    	                	 break;
    	                	 //System.out.println("Applied PAR ******************"+newHarmonyhyb[j] );
    	                	 //System.out.println(j +"PA Hybrid Applied***" +  newHarmonyhyb[j] );	
    	                 }
    	               
    	             }   
    	            // System.out.println("Random and PARhyb ******************"+r + " " + PARhyb );
    	         
    	           
    	            	if(!alreadyInNewHarmony(newHarmonyhyb,newHarmonyhyb[j],j-1))
   	                 {
   	                	done = true;
   	                	
    	            }
    	            	else
    	            	{
    	            		done = false;
    	            	
    	            	}      
    	            	
    	            	 
        	         	System.out.println("New Hybrid Harmony at "+ j);
        	         	for(int i = 0;i <=j;i++ )
        	         	{
        	         		System.out.print(" " + newHarmonyhyb[i]);
        	         	}
        	         	System.out.println("\n");
        	         	
        	         	
        	         	if(j == 27)
        	         	{
        	         	missing	= findMissing27(newHarmonyhyb);
        	         	newHarmonyhyb[27] = missing;	
        	         	j++;
        	         	break;
        	         	}
        	         	if(j == 28)
        	         	{
        	         	missing	= findMissing28(newHarmonyhyb);
        	         	newHarmonyhyb[28] = missing;	
        	         	j++;
        	         	break;
        	         	}
        	         	if(j == 29)
        	         	{
        	         	missing	= findMissing(newHarmonyhyb);
        	         	newHarmonyhyb[29] = missing;	
        	         	j++;
        	         	break;
        	         	}
        	         	
    	            	}
    	           
    	            
    	         }
    	     System.out.println("HMCRhyb and PARhyb: "+HMCRhyb+  PARhyb);
    	         
    	         // Apply Variable Limits
    	       //  NEW(k).Position=max(NEW(k).Position,VarMin);
    	       //NEW(k).Position=min(NEW(k).Position,VarMax);

    	        // Evaluation
    	         
    	         System.out.println("New Harmony is: ");
    	         for(int j = 0; j < no_of_Cloudlets; j++)
             	{
             		System.out.print(newHarmony[j] + " ");
             	} 
    	     
    	        list5.clear();
    	        
    	        for(int j = 0; j < no_of_Cloudlets; j++)
            	{
            		list5.add(map.get(newHarmony[j]));
            	}
    	     
    	       cost = HarmonySearchScheduler.calcMakespan(list5);
    	       System.out.println("\nCost of new harmony: " + cost);
    	     
    	       // Evaluation of Hybrid
  	         
  	         System.out.println("New Hybrid Harmony is: ");
  	         for(int j = 0; j < no_of_Cloudlets; j++)
           	{
           		System.out.print(newHarmonyhyb[j] + " ");
           	} 
  	     
  	        list5.clear();
  	        
  	        for(int j = 0; j < no_of_Cloudlets; j++)
          	{
          		list5.add(map.get(newHarmonyhyb[j]));
          	}
  	    
  	       cost_Hyb = EnhanceHarmonySearchScheduler.calcMakespan(list5);
  	       System.out.println("\nCost of new  hybrid harmony: " + cost_Hyb);
    	       
    	    // Merge Harmony Memory and New Harmonies
    	   //  HM=[HMNEW]; //#ok
    	     
    	     //Sort Harmony Memory
    	     //[~, SortOrder]=sort([HM.Cost]);
    	     //HM=HM(SortOrder);
    	     
    	     // Truncate Extra Harmonies
    	    // HM=HM(1:HMS);
    	     
    	     // Update Best Solution Ever Found
    	    // BestSol=HM(1);
  	       // Evaluate the new Harmony Basic
    	       System.out.println("NEW COST Basic: " + cost + " Worst Harmony Basic: " + list4[worst_Harmony]);
    	     if(cost < (list4[worst_Harmony]))
    	     {
    	    	 for(int i = 0;i < 30; i++)
    	    	 {
    	    	 init_Harmony[worst_Harmony][i]  =  newHarmony[i];
    	    	 }
    	    	 list4[worst_Harmony] = cost;
    	    	 //printHarmony();
    	     }
    	     
    	     //Evaluate the new harmony Hybrid
    	     System.out.println("NEW COST Hybrid: " + cost_Hyb + " Worst Harmony Hybrid: " + list4Hyb[worst_Harmony_Hyb]);
    	     if(cost_Hyb < (list4[worst_Harmony_Hyb]))
    	     {
    	    	 for(int i = 0;i < 30; i++)
    	    	 {
    	    	 hybrid_Harmony[worst_Harmony][i]  =  newHarmonyhyb[i];
    	    	 }
    	    	 list4Hyb[worst_Harmony_Hyb] = cost_Hyb;
    	    	 //printHarmony();
    	     }
    	     
    	     // Store Best Cost Ever Found
    	     //BestCost(it)=BestSol.Cost;
    	     
    	     // Show Iteration Information
    	     //disp(['Iteration ' num2str(it) ': Best Cost = ' num2str(BestCost(it))]);
    	  /*   for (int i = 0; i < 30; i++)
    	        {
    	        	list4[i] = list2[i].getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
    	        }*/
    	     
    	     best_index = getSmallest(list4);
    	     System.out.println("Best Cost for Basic in Iteration "+ it +": "+ list4[best_index] );
    	     
    	     best_index_Hyb = getSmallest(list4Hyb);
    	     System.out.println("Best Cost for Hybrid in Iteration "+ it +": "+ list4Hyb[best_index_Hyb] );
    	     
    	     // Damp Fret Width
    	     FW=FW*FW_damp;
    	     
    	     
    	     
    	     for(int l = 0; l < 30; l++)
    	     {
    	     newHarmonyhyb[l] = best_Five_Harmony[0][l];
    	     }
    	     }
    	 
         
        best_index = getSmallest(list4);
        best_index_Hyb = getSmallest(list4Hyb);
        worst_Harmony = getLargest(list4);
        worst_Harmony_Hyb = getLargest(list4Hyb);
        System.out.println("Final Basic Harmony is:");
        printHarmony();
        System.out.println("Final Hybrid Harmony is:");
        printHybridHarmony();
        makespanlist.clear();
        for(int i = 0; i < HMS; i++)
        {
        	list5.clear();
        	for(int j = 0; j < no_of_Cloudlets; j++)
        	{
        		list5.add(map.get(init_Harmony[i][j]));
        	}
        	double makespan = EnhanceHarmonySearchScheduler.calcMakespan(list5);
        	 System.out.println(" The MAKESPAN of Basic schedule " + i +   "is: " + makespan + "\n");
        	makespanlist.add(makespan);
        	msMAP.put(makespan, list5);
        }
        Collections.sort(makespanlist);
        System.out.println("Sorted Basic Makespans: " + makespanlist);
        
        System.out.println("Best Basic harmony is: " + makespanlist.get(0));
        System.out.println("Worst Basic harmony is: " + makespanlist.get(19));

        
        ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
        for(int i = 0; i < HMS; i++)
        {
        	cloudletList.clear();
        	for(int j = 0; j < no_of_Cloudlets; j++)
        	{
        		cloudletList.add(map.get(init_Harmony[i][j]));
        	}
        	// System.out.println(" The cloudletList is: \n" + cloudletList );
        	 setCloudletReceivedList(cloudletList);
        }
        makespanlist.clear();
        for(int i = 0; i < HMS; i++)
        {
        	list5.clear();
        	for(int j = 0; j < no_of_Cloudlets; j++)
        	{
        		list5.add(map.get(hybrid_Harmony[i][j]));
        	}
        	double makespan = EnhanceHarmonySearchScheduler.calcMakespan(list5);
        	 System.out.println(" The MAKESPAN of Hybrid schedule " + i +   "is: " + makespan + "\n");
        	makespanlist.add(makespan);
        	msMAP.put(makespan, list5);
        }
        Collections.sort(makespanlist);
        System.out.println("Sorted Hybrid Makespans: " + makespanlist);
        
        System.out.println("Best Hybrid harmony is: " + makespanlist.get(0));
        System.out.println("Worst Hybrid harmony is: " + makespanlist.get(19));
        ArrayList<Cloudlet> cloudletListHyb = new ArrayList<Cloudlet>();
        for(int i = 0; i < HMS; i++)
        {
        	cloudletListHyb.clear();
        	for(int j = 0; j < no_of_Cloudlets; j++)
        	{
        		cloudletListHyb.add(map.get(init_Harmony[i][j]));
        	}
        	// System.out.println(" The cloudletList is: \n" + cloudletList );
        	 setCloudletReceivedList(cloudletListHyb);
        }
        
        
        
        
      
        
        
      
        //System.out.println("\n\tSJFS Broker Schedules\n");
        //System.out.println("\n");
    }

    public void printNumber(Cloudlet[] list) {
        for (int i = 0; i < list.length; i++) {
            System.out.print(" " + list[i].getCloudletId());
            System.out.println(list[i].getCloudletStatusString());
        }
        System.out.println();
    }

    public void printNumbers(ArrayList<Cloudlet> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.print(" " + list.get(i).getCloudletId());
        }
        System.out.println();
    }

    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
                + " received");
        cloudletsSubmitted--;
        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) {
            scheduleTaskstoVms();
            cloudletExecution(cloudlet);
        }
    }

    protected void cloudletExecution(Cloudlet cloudlet) {

        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
            Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
            clearDatacenters();
            finishExecution();
        } else { // some cloudlets haven't finished yet
            if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
                // all the cloudlets sent finished. It means that some bount
                // cloudlet is waiting its VM be created
                clearDatacenters();
                createVmsInDatacenter(0);
            }
        }
    }

    @Override
    protected void processResourceCharacteristics(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            distributeRequestsForNewVmsAcrossDatacenters();
        }
    }

    protected void distributeRequestsForNewVmsAcrossDatacenters() {
        int numberOfVmsAllocated = 0;
        int i = 0;

        final List<Integer> availableDatacenters = getDatacenterIdsList();

        for (Vm vm : getVmList()) {
            int datacenterId = availableDatacenters.get(i++ % availableDatacenters.size());
            String datacenterName = CloudSim.getEntityName(datacenterId);

            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                numberOfVmsAllocated++;
            }
        }

        setVmsRequested(numberOfVmsAllocated);
        setVmsAcks(0);
    }
    
    
    
    
    
    
    public static void createInitHarmony()
    {
    	
    	EnhanceHarmonyRandomPermutation rp = new EnhanceHarmonyRandomPermutation();
        ArrayList<Integer> v = new ArrayList<>(no_of_Cloudlets);
        System.out.println(" The initial Harmony is: \n" );
        for(int j = 0;j < HMS;j++ )
        {
        v = rp.generateRandom(no_of_Cloudlets);
        for(int k=0; k < no_of_Cloudlets; k++)
        {
        init_Harmony[j][k] = v.get(k);
        }
        System.out.println(" "+ v );
        }
    	
        
        
    }
    public static int getSmallest(Double[] a){  
    	
    	int smallest_index = 0;  
    	for (int i = 0; i < 19; i++)   
    	        {  

            if (a[smallest_index] >= a[i])   
            {  
               
                smallest_index =  i;
            } 
        }  
    	       return smallest_index;  
    	}
    
    public static int getLargest(Double[] a){  
    	
    	int largest_index = 0;  
    	for (int i = 0; i <= 19; i++)   
    	        {  
    	            
    	                if (a[largest_index] <= a[i])   
    	                {  
    	                   
    	                    largest_index =  i;
    	                } 
    	            }  
    	          
    	       return largest_index;  
    	}
    
     public static boolean alreadyInNewHarmony(int[] newHar,int val, int index)
     {
    	 for (int i = 0;i < index ; i++) {
    		    if (newHar[i] == val) {
    		        return true;
    		    }
    		}
    	 return false;
    	
     }
     public static void printHarmony()
     {
    	 for (int i = 0; i < HMS; i++)
         {
         	for(int j = 0; j< no_of_Cloudlets;j++)
         	{
         		System.out.print(init_Harmony[i][j] + " ");	
         	}
         	System.out.println();
         			}

    	 
     }
     public static void printHybridHarmony()
     {
    	 for (int i = 0; i < HMS; i++)
         {
         	for(int j = 0; j< no_of_Cloudlets;j++)
         	{
         		System.out.print(hybrid_Harmony[i][j] + " ");	
         	}
         	System.out.println();
         			}

    	 
     }
     public static int findMissing(int[] arr)
     {
    	 int i,j = 0,miss = 1;
    	 
    	 for(i = 0; i<30 ;i++)
    	 {
    		 for(j = 0; j<29 ;j++)
    		 {
    		 if(arr[j] == i)
    		 break;
    		 }
    		 if(j == 29)
        		 miss = i;
    	 }	 
    	 
    return miss;
    	
     }
     public static int findMissing28(int[] arr)
     {
    	 int i,j = 0,miss = 1;
    	 
    	 for(i = 0; i<30 ;i++)
    	 {
    		 for(j = 0; j<28 ;j++)
    		 {
    		 if(arr[j] == i)
    		 break;
    		 }
    		 if(j == 28)
        		 miss = i;
    	 }	 
    	 
    return miss;
    	
     }
     public static int findMissing27(int[] arr)
     {
    	 int i,j = 0,miss = 1;
    	 
    	 for(i = 0; i<30 ;i++)
    	 {
    		 for(j = 0; j<27 ;j++)
    		 {
    		 if(arr[j] == i)
    		 break;
    		 }
    		 if(j == 27)
        		 miss = i;
    	 }	 
    	 
    return miss;
    	
     }
}
   
