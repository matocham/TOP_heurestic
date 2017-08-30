package pl.edu.pb.graph;

/**
 * Created by Mateusz on 08.05.2017.
 */
public class Graph {
    static double[][] graph;

    public static void initalize(Point[] points, boolean toInt, boolean onEarth) {
        graph = new double[points.length][points.length];
        for (Point n : points) {
            for (Point second : points) {
                if (n.equals(second)) {
                    graph[n.getId()][n.getId()] = 0;
                } else {
                    if (onEarth) {
                        graph[n.getId()][second.getId()] = getDistanceFromLatLonInKm(n.getY(), n.getX(), second.getY(), second.getX(), toInt);
                    } else {
                        graph[n.getId()][second.getId()] = getDistance(n, second, toInt);
                    }
                }
            }
        }
    }

    private static double getDistance(Point a, Point b, boolean asInt) {
        double result = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
        if (asInt) {
            return (int) result;
        } else {
            return result;
        }
    }

    public static double distanceBetween(int a, int b) {
        return graph[a][b];
    }

    public static double distanceBetween(Point a, Point b) {
        return graph[a.getId()][b.getId()];
    }

    /**
     * longitude to długość
     * latitude to szerokość geograficzna
     */
    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2, boolean toInt) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        if (toInt) {
            return (int) d;
        } else {
            return d;
        }
    }

    private static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }
}
