package com.docaposte.test.elevators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import com.docaposte.test.elevators.models.Elevator;

public class Building {
	
	/** number of floors*/
	private int numberOfFloors;
	/** list of elevators in building*/
	private List<Elevator> listOfElevators;
	/** identifier for a stopping elevator*/
	private int valStepOfStop = -1;
	
	public Building(int numberOfFloors, String... listOfElevators) {
		this.numberOfFloors = numberOfFloors;
		
		//fill the list of elevators
		this.listOfElevators = new ArrayList<Elevator>();
		for(String elevatorStr : listOfElevators) {
    		this.listOfElevators.add(getElevatorDetails(elevatorStr));
    	}
	}
	
	/**
	 * Mapping the data of an elevator
	 * 
	 * @param elevatorStr
	 * @return
	 */
	private Elevator getElevatorDetails(String elevatorStr) {
		Elevator elevator = new Elevator();
		int positionTwoPoint = elevatorStr.indexOf(":");
		elevator.setId(elevatorStr.substring(0, positionTwoPoint));
		elevator.setCurrentPosition(Integer.parseInt(elevatorStr.substring(positionTwoPoint+1)));
		elevator.setDirection("");
		
		return elevator;
	}
	
	/**
	 * Choose a direction for an elevator
	 * 
	 * @param idElevator
	 * @param direction
	 */
	public void move(String idElevator, String direction) {
		Predicate<Elevator> filterById = elevator -> elevator.getId().equals(idElevator);
		listOfElevators.stream().filter(filterById).forEach(elevator -> { elevator.setDirection(direction); });
	}
	
	/**
	 * Stopping an elevator in given position 
	 * 
	 * @param idElevator
	 * @param numberOfFloorToStop
	 */
	public void stopAt(String idElevator, int numberOfFloorToStop) {
		Predicate<Elevator> filterById = elevator -> elevator.getId().equals(idElevator);
		listOfElevators.stream().filter(filterById).forEach(elevator -> { elevator.setStopAt(numberOfFloorToStop); });
	}
	
	/**
	 * Returning the nearest elevator for the demander
	 * 
	 * @param floorDemanded
	 * @return
	 */
	public String requestElevator(int ...floorDemanded) {
		
		Elevator elevatorForService = new Elevator();
		int nbStepsMin = valStepOfStop;
		
		// Choose the number of floors
		int nbFloorDemanded = numberOfFloors;
		if(floorDemanded.length > 0) {
			nbFloorDemanded = floorDemanded[0];
		}
		
		//finding the nearest elevator among the list of elevators 
		for (Elevator elevator : listOfElevators) {
			int nbStepsOfElevator = getNumberStepsOfElevatorToFloorDemanded(elevator, nbFloorDemanded);
			if((nbStepsOfElevator > valStepOfStop && nbStepsOfElevator < nbStepsMin) 
					|| nbStepsMin == valStepOfStop && nbStepsOfElevator > valStepOfStop) {
				nbStepsMin = nbStepsOfElevator;
				elevatorForService = elevator;
			}
		}	

		return elevatorForService.getId();
	}
	
	/**
	 * Determine the number of steps for an elevator for arriving to the demanded floor
	 * 
	 * @param elevator
	 * @param nbFloor
	 * @return
	 */
	private int getNumberStepsOfElevatorToFloorDemanded(Elevator elevator, int floorDemanded) {
		int step = valStepOfStop;	
		if(floorDemanded > elevator.getCurrentPosition() && (elevator.getDirection().isEmpty() || elevator.getDirection().equals("UP"))) {
			//condition only up && don't stop or stop after
			if(elevator.getStopAt() == 0 || elevator.getStopAt() >= floorDemanded || elevator.getStopAt() < elevator.getCurrentPosition()) {
				step = floorDemanded - elevator.getCurrentPosition();
			}	
			
		} else if(floorDemanded < elevator.getCurrentPosition() && (elevator.getDirection().isEmpty() || elevator.getDirection().equals("DOWN"))) {
			//condition only down && don't stop or stop after
			if(elevator.getStopAt() == 0 || elevator.getStopAt() <= floorDemanded || elevator.getStopAt() > elevator.getCurrentPosition()) {
				step = elevator.getCurrentPosition() - floorDemanded;
			}	
			
		} else if(floorDemanded < elevator.getCurrentPosition() && elevator.getDirection().equals("UP")) {
			//condition up/down - change direction && don't stop or stop after
			if(elevator.getStopAt() == 0 || (elevator.getStopAt() < elevator.getCurrentPosition()  && elevator.getStopAt() <= floorDemanded)) {
				step = (numberOfFloors - elevator.getCurrentPosition()) + (numberOfFloors - floorDemanded);
			}
			
		} else if(floorDemanded > elevator.getCurrentPosition() && elevator.getDirection().equals("DOWN")) {
			//condition down/up - change direction && don't stop or stop after
			if(elevator.getStopAt() == 0 || (elevator.getStopAt() > elevator.getCurrentPosition()  && elevator.getStopAt() >= floorDemanded)) {
				step = (elevator.getCurrentPosition() - 1) + (floorDemanded - 1);
			}
			
		}
		
		return step;
	}
	
}
