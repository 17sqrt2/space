package com.space.controller;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "rest/")
public class ShipController {

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private ShipService shipService;

	@RequestMapping(path = "/ships", method = RequestMethod.GET)
	@ResponseBody
	public List<Ship> getSortedShipsList(@RequestParam(required = false) String name,
										 @RequestParam(required = false) String planet,
										 @RequestParam(required = false) String shipType,
										 @RequestParam(required = false) Long after,
										 @RequestParam(required = false) Long before,
										 @RequestParam(required = false) Boolean isUsed,
										 @RequestParam(required = false) Double minSpeed,
										 @RequestParam(required = false) Double maxSpeed,
										 @RequestParam(required = false) Integer minCrewSize,
										 @RequestParam(required = false) Integer maxCrewSize,
										 @RequestParam(required = false) Double minRating,
										 @RequestParam(required = false) Double maxRating,
										 @RequestParam(required = false) Integer pageNumber,
										 @RequestParam(required = false) Integer pageSize,
										 @RequestParam(required = false) String order) {

		List<Ship> filteredList = shipService.getFilteredShipsList(
				name, planet, shipType, after,
				before, isUsed, minSpeed, maxSpeed,
				minCrewSize, maxCrewSize, minRating, maxRating);

		return shipService.getSortedShipsList(filteredList, pageNumber, pageSize, order);
	}

	@RequestMapping(path = "/ships/count", method = RequestMethod.GET)
	@ResponseBody
	public Integer count(@RequestParam(required = false) String name,
						 @RequestParam(required = false) String planet,
						 @RequestParam(required = false) String shipType,
						 @RequestParam(required = false) Long after,
						 @RequestParam(required = false) Long before,
						 @RequestParam(required = false) Boolean isUsed,
						 @RequestParam(required = false) Double minSpeed,
						 @RequestParam(required = false) Double maxSpeed,
						 @RequestParam(required = false) Integer minCrewSize,
						 @RequestParam(required = false) Integer maxCrewSize,
						 @RequestParam(required = false) Double minRating,
						 @RequestParam(required = false) Double maxRating) {

		return shipService.getFilteredShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
				minCrewSize, maxCrewSize, minRating, maxRating).size();
	}

	@RequestMapping(path = "/ships/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Ship getShip(@PathVariable Long id) {

		return shipService.getShipById(id);
	}

	@RequestMapping(path = "/ships/", method = RequestMethod.POST)
	@ResponseBody
	public Ship createShip(@RequestBody Ship newShip) {
		Ship ship = shipService.createShip(newShip);
		shipRepository.save(ship);
		return ship;
	}

	@RequestMapping(path = "/ships/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Ship updateShip(@RequestBody Ship newShip, @PathVariable Long id) {
		Ship shipToUpdate = shipService.getShipById(id);
		if (shipService.updateShip(shipToUpdate, newShip)) {
			shipRepository.save(shipToUpdate);
		}
		return shipToUpdate;
	}

	@RequestMapping(path = "/ships/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteShip(@PathVariable Long id) {
		Ship ship = shipService.getShipById(id);
		shipRepository.delete(getShip(id));
	}
}
