package org.cloudbus.cloudsim.examples;


/****
 * @author Sandeep Patel 
 * 
 * */


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class RoundRobinScheduler1 {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        LinkedList<Vm> list = new LinkedList<Vm>();

        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(dcId + 2);
            list.add(cloudlet[i]);
        }
        return list;
    }

    private static void printVmList(List<Vm> vmList) {
        int size = vmList.size();
        Vm vm;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== VIRTUAL MACHINE LIST ==========");
        Log.printLine("VM ID" + indent + "MIPS" +
                      indent + "RAM" + indent + "Bandwidth" + 
                      indent + "Size" + indent + "VMM");

        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);
        for (int i = 0; i < size; i++) {
            vm = vmList.get(i);
            Log.print(indent + dft.format(vm.getId()) + indent + indent +
                      dft.format(vm.getMips()) + indent + 
                      dft.format(vm.getRam()) + indent +
                      dft.format(vm.getBw()) + indent + 
                      dft.format(vm.getSize()) + indent +
                      vm.getVmm());
            Log.printLine();
        }
    } 
    
    
    
    public static void main(String[] args) {
        Log.printLine("Starting Round Robin Scheduler...");

        new GenerateMatrices();
        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        try {
            int num_user = 1; // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }

            // Third step: Create Broker
            RoundRobinDatacenterBroker1 broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            // Fourth step: Create VMs and Cloudlets and send them to broker
            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);
           
            // Print VM list before starting the simulation
            printVmList(vmList);
            
            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // Print Cloudlet Results
            printCloudletList(newList);

            // Calculate and print throughput and total cost
            double throughput = calculateThroughput(newList);
            double totalCost = calculateTotalCost(vmList, newList);

            Log.printLine("Throughput: " + throughput + " tasks per second");
            Log.printLine("Total Cost: " + totalCost + " $");

            Log.printLine(RoundRobinScheduler1.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static RoundRobinDatacenterBroker1 createBroker(String name) throws Exception {
        return new RoundRobinDatacenterBroker1(name);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + indent + "Time" +
                indent + "Start Time" +
                indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) +
                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
        double makespan = calcMakespan(list);
        Log.printLine("Makespan using RR: " + makespan);
    }

    private static double calcMakespan(List<Cloudlet> list) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = list.get(i).getVmId() % Constants.NO_OF_DATA_CENTERS;
            if (dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }

    private static double calculateThroughput(List<Cloudlet> cloudletList) {
        int completedTasks = 0;
        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                completedTasks++;
            }
        }
        double makespan = calcMakespan(cloudletList);
        return completedTasks / makespan; // Throughput = tasks / makespan (time)
    }

    private static double calculateTotalCost(List<Vm> vmList, List<Cloudlet> cloudletList) {
        double totalCost = 0.0;
        double vmCostPerHour = 0.05; // Example cost per VM hour
        double cloudletCostPerTask = 0.02; // Example cost per cloudlet task

        for (Vm vm : vmList) {
            double vmRunningTime = 0.0;
            for (Cloudlet cloudlet : cloudletList) {
                if (cloudlet.getVmId() == vm.getId()) {
                    vmRunningTime += cloudlet.getActualCPUTime(); // Assuming this is the VM running time
                }
            }
            totalCost += (vmRunningTime / 3600) * vmCostPerHour; // Cost per VM for the time it runs
        }

        for (Cloudlet cloudlet : cloudletList) {
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                totalCost += cloudletCostPerTask; // Add the cost per task
            }
        }

        return totalCost;
    }
}
