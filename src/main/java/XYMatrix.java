import java.util.*;

public class XYMatrix {

    private Map<Integer, List<Integer>> values = new HashMap();

    public int put(int x, int value) {
        if (values.containsKey(x)) {
            values.get(x).add(value);
        } else {
            values.put(x, new ArrayList<Integer>());
            values.get(x).add(value);
        }
        return values.get(x).indexOf(value);
    }

    public int get(int x, int y) {
        if (values.containsKey(x)) {
            if (values.get(x).size() > y - 1) {
                return values.get(x).get(y - 1);
            }
        }
        return 0;
    }

    public boolean containsXY(int x, int y) {
        return values.containsKey(x) ? values.get(x).size() > y : false;
    }

    public boolean remove(int x, int y) {
        if (values.containsKey(x)) {
            if (values.get(x).size() > y) {
                values.get(x).remove(y);
                return true;
            }
        }
        return false;
    }

    public Set<Map.Entry<Integer, List<Integer>>> entrySet() {
        return values.entrySet();
    }

    public XYMatrix rotate() {
        XYMatrix xyMatrix = new XYMatrix();
        for (int i = 0; i < values.get(1).size(); i++) {
            for (int y = 1; y <= values.size(); y++) {
                xyMatrix.put(i+1, values.get(y).get(i));
            }
        }
        return xyMatrix;
    }

    public XYMatrix snipMatrix(Integer x, Integer y, Integer range) {
        XYMatrix snippetMatrix = new XYMatrix();

        for (int i = -range - 1; i < range; i++) {
            for (int j = -range - 1; j < range; j++) {
                if (x + i >= 0 && (y - 1) + j >= 0) {
                    if (this.get(x + i, y + j) != 0) { //ACHTUNG
                        snippetMatrix.put(x + i, this.get(x + i, y + j));
                    }
                }
            }
        }
        return snippetMatrix;
    }

    public Integer findMaxValue() {
        Integer max = -Integer.MAX_VALUE;
        for (Map.Entry<Integer, List<Integer>> entry : values.entrySet()) {
            for (Integer i : entry.getValue()) {
                max = i > max ? i : max;
            }
        }
        return max;
    }

    public Integer[] findXYMaxinRange(Integer x, Integer y, Integer range) {
        Integer max = snipMatrix(x, y, range).findMaxValue();
        for (int i = -range - 1; i < range; i++) {
            for (int j = -range - 1; j < range; j++) {
                if (x + i >= 0 && (y - 1) + j >= 0) {
                    if (this.get(x + i, y + j) == max) {
                        return new Integer[]{x + i, y + j};
                    }
                }

            }
        }
        return new Integer[]{0, 0};
    }

    public XYGraph findGraphInRow(Integer x, Integer y) {
        List<XYGraph> XYGraphs = new ArrayList<XYGraph>();
        int graphNumber = -1;
        List<Integer> row = values.get(x);
        Integer currentValue = row.get(0);
        int gradiantState = -1;
        Integer yTrack = 1;
        for (Integer nextValue : row) {
            if (nextValue > currentValue && gradiantState != 0) {
                gradiantState = 2;
            }
            if (nextValue < currentValue && gradiantState != 1) {
                gradiantState = 1;
            }

            if (gradiantState == 0) {
                XYGraphs.get(graphNumber).addValueZ(nextValue);
                XYGraphs.get(graphNumber).addValueY(yTrack++);
            }

            if (gradiantState == 1) {
                XYGraphs.get(graphNumber).addValueZ(nextValue);
                XYGraphs.get(graphNumber).addValueY(yTrack++);
            }

            if (gradiantState == 2) {
                gradiantState = 0;
                graphNumber++;
                XYGraphs.add(new XYGraph(x));
                XYGraphs.get(graphNumber).addValueZ(nextValue);
                XYGraphs.get(graphNumber).addValueY(yTrack++);
            }

            if (gradiantState == -1) {
                gradiantState = row.get(0) < row.get(1) ? 0 : 1;
                graphNumber++;
                XYGraphs.add(new XYGraph(x));
                XYGraphs.get(graphNumber).addValueZ(nextValue);
                XYGraphs.get(graphNumber).addValueY(yTrack++);
            }
            currentValue = nextValue;

        }

        for (XYGraph g : XYGraphs) {
            if (g.containsValueY(y)) return g;
        }
        return null;

    }

    public XYHill findHill(Integer x, Integer y) {
        return new XYHill(findGraphInRow(x,y), rotate().findGraphInRow(y,x), x, y );
    }

    //UNUSED
    public List<XYGraph> findXGraphs() {
        List<XYGraph> XYGraphs = new ArrayList<XYGraph>();
        int graphNumber = -1;

        for (Map.Entry<Integer, List<Integer>> entry : entrySet()) {
            Integer currentValue = entry.getValue().get(0);
            int gradiantState = -1;
            Integer yTrack = 1;
            for (Integer nextValue : entry.getValue()) {
                if (nextValue > currentValue && gradiantState != 0) {
                    gradiantState = 2;
                }
                if (nextValue < currentValue && gradiantState != 1) {
                    gradiantState = 1;
                }

                if (gradiantState == 0) {
                    XYGraphs.get(graphNumber).addValueZ(nextValue);
                    XYGraphs.get(graphNumber).addValueY(yTrack++);
                }

                if (gradiantState == 1) {
                    XYGraphs.get(graphNumber).addValueZ(nextValue);
                    XYGraphs.get(graphNumber).addValueY(yTrack++);
                }

                if (gradiantState == 2) {
                    gradiantState = 0;
                    graphNumber++;
                    XYGraphs.add(new XYGraph(entry.getKey()));
                    XYGraphs.get(graphNumber).addValueZ(nextValue);
                    XYGraphs.get(graphNumber).addValueY(yTrack++);
                }

                if (gradiantState == -1) {
                    gradiantState = entry.getValue().get(0) < entry.getValue().get(1) ? 0 : 1 ;
                    graphNumber++;
                    XYGraphs.add(new XYGraph(entry.getKey()));
                    XYGraphs.get(graphNumber).addValueZ(nextValue);
                    XYGraphs.get(graphNumber).addValueY(yTrack++);
                }
                currentValue = nextValue;

            }

        }
        return XYGraphs;
    }

    public List<XYHill> getHills(Map<Integer, Integer> points) {
        List<XYHill> hills = new ArrayList<XYHill>();

        for (Map.Entry<Integer, Integer> entry : points.entrySet()) {
            Integer[] maximumPoints = findXYMaxinRange(entry.getKey(), entry.getValue(), 10);
            Integer x = maximumPoints[0];
            Integer y = maximumPoints[1];
            XYHill hill = findHill(x, y);
            hills.add(hill);
        }
        return hills;
    }


    @Override
    public String toString() {
        String ret = "";
        for (Map.Entry<Integer, List<Integer>> entry : values.entrySet()) {
            for (Integer e : entry.getValue()) {
                ret += e + "\t";
            }
            ret += "\n";
        }
        return ret;
    }

}
