package com.space.service;

import com.space.model.Ship;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShipService {
	public List<Ship> getFilteredShipsList(String name,
										   String planet,
										   String shipType,
										   Long after,
										   Long before,
										   Boolean isUsed,
										   Double minSpeed,
										   Double maxSpeed,
										   Integer minCrewSize,
										   Integer maxCrewSize,
										   Double minRating,
										   Double maxRating);


	public List<Ship> getSortedShipsList(List<Ship> list,
										 Integer pageNumber,
										 Integer pageSize,
										 String order);

	public Ship createShip(Ship ship);

	public boolean updateShip(Ship ship, Ship newShip);

	public Ship getShipById(Long id);
}
