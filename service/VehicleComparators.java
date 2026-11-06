package fleet.service;

import fleet.model.Vehicle;

import java.util.Comparator;

public final class VehicleComparators {
    private VehicleComparators() {
    }

    public static final Comparator<Vehicle> BY_BRAND_MODEL = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle first, Vehicle second) {
            int byBrand = first.getBrand().compareToIgnoreCase(second.getBrand());
            if (byBrand != 0) {
                return byBrand;
            }
            int byModel = first.getModel().compareToIgnoreCase(second.getModel());
            if (byModel != 0) {
                return byModel;
            }
            return first.getId().compareTo(second.getId());
        }
    };

    public static final Comparator<Vehicle> BY_PRICE_DESC = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle first, Vehicle second) {
            return Integer.compare(second.getPrice(), first.getPrice());
        }
    };

    public static final Comparator<Vehicle> BY_YEAR_DESC = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle first, Vehicle second) {
            return Integer.compare(second.getYear(), first.getYear());
        }
    };

    public static final Comparator<Vehicle> BY_TYPE_THEN_PRICE = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle first, Vehicle second) {
            int byType = first.getVehicleType().compareToIgnoreCase(second.getVehicleType());
            if (byType != 0) {
                return byType;
            }
            return Integer.compare(second.getPrice(), first.getPrice());
        }
    };

    public static final Comparator<Vehicle> BY_DAILY_RENTAL_COST = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle first, Vehicle second) {
            double firstCost = first.calculateRentalCost(1);
            double secondCost = second.calculateRentalCost(1);
            return Double.compare(firstCost, secondCost);
        }
    };
}
