package bomberman.server;

import bomberman.server.elements.Element;
import bomberman.server.elements.Wall;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board {

    private List<Element> elements;
    private int cols = 15;
    private int rows = 15;
    private double probability_wall = 0.3;

    public void generate() {
        this.elements = new ArrayList<Element>();
        int size = this.cols * this.rows;

        for (int i = 0; i < size; i++) {
            Element element = null;

            if (i <= this.rows || i % this.rows == 0 || (i + 1) % this.rows == 0 || size - this.rows < i) {
                Wall wall = new Wall();
                wall.setBreakable(false);
                element = wall;
            } else if (Math.random() < this.probability_wall) {
                element = new Wall();
            }

            this.elements.add(element);
        }
    }

    public List<Map> getData() {
        List<Map> data = new ArrayList<Map>();
        int size = this.elements.size();

        for (int i = 0; i < size; i++) {
            try {
                data.add(Element.export(this.elements.get(i)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return data;
    }

    public int getCols() {
        return this.cols;
    }

    public int getRows() {
        return this.rows;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElement(int index, Element element) {
        this.elements.set(index, element);
    }
}
