package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {
	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 3;

	@Autowired
	ShipRepository shipRepository;

	public Comparator<Ship> getComparatorByOrder(String order) {
		Comparator<Ship> comparator = null;
		switch (ShipOrder.valueOf(order)) {
			case ID:
				comparator = Comparator.comparing(Ship::getId);
				break;
			case DATE:
				comparator = Comparator.comparing(Ship::getProdDate);
				break;
			case SPEED:
				comparator = Comparator.comparing(Ship::getSpeed);
				break;
			case RATING:
				comparator = Comparator.comparing(Ship::getRating);
				break;
			default:
				break;
		}
		return comparator;
	}

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
										   Double maxRating) {

		List<Ship> filteredShipsList = shipRepository.findAll();

		if (name != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getName().toLowerCase()
							.contains(name.toLowerCase()))
					.collect(Collectors.toList());

		if (planet != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getPlanet().toLowerCase()
							.contains(planet.toLowerCase()))
					.collect(Collectors.toList());

		if (shipType != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getShipType().equals(ShipType.valueOf(shipType)))
					.collect(Collectors.toList());

		if (after != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getProdDate().after(new Date(after)))
					.collect(Collectors.toList());

		if (before != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getProdDate().before(new Date(before)))
					.collect(Collectors.toList());

		if (isUsed != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getUsed().equals(isUsed))
					.collect(Collectors.toList());

		if (minSpeed != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getSpeed() >= minSpeed)
					.collect(Collectors.toList());

		if (maxSpeed != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getSpeed() <= maxSpeed)
					.collect(Collectors.toList());

		if (minCrewSize != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getCrewSize() >= minCrewSize)
					.collect(Collectors.toList());

		if (maxCrewSize != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getCrewSize() <= maxCrewSize)
					.collect(Collectors.toList());

		if (minRating != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getRating() >= minRating)
					.collect(Collectors.toList());

		if (maxRating != null)
			filteredShipsList = filteredShipsList.stream()
					.filter(ship -> ship.getRating() <= maxRating)
					.collect(Collectors.toList());

		return filteredShipsList;
	}

	public List<Ship> getSortedShipsList(List<Ship> list,
										 Integer pageNumber,
										 Integer pageSize,
										 String order) {
		Integer curPageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber;
		Integer curPageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
		String curOrder = order == null ? ShipOrder.ID.toString() : order;

		return list.stream()
				.sorted(getComparatorByOrder(curOrder))
				.skip(curPageNumber * curPageSize)
				.limit(curPageSize)
				.collect(Collectors.toList());
	}

	public boolean checkBodyParamsIsNull(Ship ship) {
		if (ship.getName() == null
				|| ship.getPlanet() == null
				|| ship.getShipType() == null
				|| ship.getProdDate() == null
				|| ship.getSpeed() == null
				|| ship.getCrewSize() == null) {
			return true;
		}
		return false;
	}

	public boolean checkNameLengthTooLong(Ship ship) {
		if (ship.getName() != null) {
			if (ship.getName().length() > 50) {
				return true;
			}
		}
		return false;
	}

	public boolean checkPlanetLengthTooLong(Ship ship) {
		if (ship.getPlanet().length() > 50) {
			return true;
		}
		return false;
	}

	public boolean checkNameIsEmpty(Ship ship) {
		if (ship.getName().equals("")) {
			return true;
		}
		return false;
	}

	public boolean checkPlanetIsEmpty(Ship ship) {
		if (ship.getPlanet().equals("")) {
			return true;
		}
		return false;
	}

	public boolean checkSpeedIsNotCorrect(Ship ship) {
		if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99) {
			return true;
		}
		return false;
	}

	public boolean checkCrewSizeIsNotCorrect(Ship ship) {
		if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {
			return true;
		}
		return false;
	}

	public boolean checkProdDateParamIsNotCorrect(Ship ship) {
		if (ship.getProdDate().getTime() < 0) {
			return true;
		}

		Calendar dateMinCal = Calendar.getInstance();
		Calendar dateMaxCal = Calendar.getInstance();

		dateMinCal.set(2800, Calendar.JANUARY, 1);
		dateMaxCal.set(3019, Calendar.DECEMBER, 31);

		if (ship.getProdDate().getTime() < dateMinCal.getTimeInMillis()
				|| ship.getProdDate().getTime() > dateMaxCal.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	public Double calculateRating(Ship ship) {
		if (ship.getProdDate() != null && ship.getSpeed() != null) {
			boolean isUsed = ship.getUsed() != null ? ship.getUsed() : false;
			double k = isUsed ? 0.5 : 1D;
			int prodYear = calcYear(ship.getProdDate());
			double ratingNoRound = (80 * ship.getSpeed() * k) / (3019 - prodYear + 1);
			return (double) Math.round(ratingNoRound * 100) / 100;
		}
		return null;
	}

	public Ship createShip(Ship ship) {
		if (ship == null) throw new BadRequestException();

		Ship newShip = new Ship();

		newShip.setUsed(ship.getUsed() != null ? ship.getUsed() : false);

		if (checkBodyParamsIsNull(ship)
				|| checkNameIsEmpty(ship)
				|| checkPlanetIsEmpty(ship)
				|| checkNameLengthTooLong(ship)
				|| checkPlanetLengthTooLong(ship)
				|| checkSpeedIsNotCorrect(ship)
				|| checkCrewSizeIsNotCorrect(ship)
				|| checkProdDateParamIsNotCorrect(ship)
		) throw new BadRequestException();

		newShip.setName(ship.getName());
		newShip.setPlanet(ship.getPlanet());
		newShip.setShipType(ship.getShipType());
		newShip.setProdDate(ship.getProdDate());
		newShip.setSpeed(ship.getSpeed());
		newShip.setCrewSize(ship.getCrewSize());
		newShip.setRating(calculateRating(ship));

		return newShip;
	}

	public boolean updateShip(Ship ship, Ship newShip) {
		try {
			newShip.equals(new Ship());
		} catch (NullPointerException e) {
			return false;
		}

		if (ship != null) {
			if (newShip.getName() != null) {
				if (checkNameIsEmpty(newShip) || checkNameLengthTooLong(newShip))
					throw new BadRequestException();
				ship.setName(newShip.getName());
			}

			if (newShip.getPlanet() != null) {
				if (checkPlanetIsEmpty(newShip) || checkPlanetLengthTooLong(newShip))
					throw new BadRequestException();
				ship.setPlanet(newShip.getPlanet());
			}

			if (newShip.getShipType() != null)
				ship.setShipType(newShip.getShipType());

			if (newShip.getProdDate() != null) {
				if (checkProdDateParamIsNotCorrect(newShip))
					throw new BadRequestException();
				ship.setProdDate(newShip.getProdDate());
			}

			newShip.setUsed(newShip.getUsed() != null ? newShip.getUsed() : false);

			if (newShip.getSpeed() != null) {
				if (checkSpeedIsNotCorrect(newShip))
					throw new BadRequestException();
				ship.setSpeed(newShip.getSpeed());
			}

			if (newShip.getCrewSize() != null) {
				if (checkCrewSizeIsNotCorrect(newShip))
					throw new BadRequestException();
				ship.setCrewSize(newShip.getCrewSize());
			}

			ship.setRating(calculateRating(ship));
			return true;
		} else {
			return false;
		}
	}

	public Ship getShipById(Long id) {
		if (id <= 0) {
			throw new BadRequestException();
		}

		return shipRepository.findAll().stream()
				.filter(ship -> ship.getId().equals(id))
				.findFirst()
				.orElseThrow(NotFoundException::new);
	}

	private int calcYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		return cal.get(Calendar.YEAR);
	}
}
