package de.randomerror.fh.tsp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Visualizer extends JFrame {
    private Graph graph;
    private Trainer trainer;
    public double maxY = 0;

    public Visualizer(Graph graph, Trainer trainer, KeyListener l) {
        super("TSP");
        this.graph = graph;
        this.trainer = trainer;
        VCan contentPane = new VCan();
        addKeyListener(l);
        this.setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    class VCan extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            List<Node> nodes = graph.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawArc(node.getX() - 3, node.getY() - 3, 6, 6, 0, 360);
             //   g2d.drawString("" + i, node.getX() - 15 + 10, node.getY() - 15 + 18);

            }

            List<Node> renderPath = graph.getDisplayPath().dulicate().solution;
            if(renderPath == null)
                return;
            renderPath.add(renderPath.get(0));
            if (renderPath.size() - 1 != graph.getNodes().size())
                return;

            for (int i = 1; i < renderPath.size(); i++) {
                Node n0 = renderPath.get(i - 1);
                Node n1 = renderPath.get(i);

                g2d.drawLine(n0.getX(), n0.getY(), n1.getX(), n1.getY());
            }

            g2d.drawString("Length: " + graph.getDisplayPath().getLength() + " Gen: " + trainer.getGeneration(), 5, 20);


            List<Path> population = trainer.getPopulation();

            List<Double> populationYs = population.stream()
                    .sorted(Comparator.comparingDouble(path -> -path.getLength()))
                    .mapToDouble(Path::getLength)
                    .boxed()
                    .collect(Collectors.toList());

            if (maxY == 0) {
                maxY = populationYs.get(0);
            }

            drawGraph(g2d, maxY, 10, 230, populationYs);

            List<Double> bestGraphYs = trainer.getBestOfEachGeneration()
                    .stream()
                    .mapToDouble(Path::getLength)
                    .boxed()
                    .collect(Collectors.toList());
            drawGraph(g2d, bestGraphYs.get(0), 220, 230, bestGraphYs);
        }


        private void drawGraph(Graphics2D g2d, double maxY, int x, int y, List<Double> yList) {
            int scale = 200;
            g2d.setColor(Color.BLACK);
            drawArrow(g2d, x - scale/50, y, x + scale, y);
            drawArrow(g2d, x, y + scale/50, x, y - scale);

            g2d.setColor(Color.RED);

            int maxX = yList.size();
            for (int i = 0; i < maxX; i++) {
                double xPoint = x + ((double)i/maxX)*scale;
                double yPoint = y - (yList.get(i)/maxY)*scale;

                g2d.drawArc((int)xPoint, (int)yPoint, 1, 1,0, 360);
            }
        }

        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            int ARROW_SIZE = 4;
            AffineTransform transformBefore = g2d.getTransform();
            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.sqrt(dx*dx + dy*dy);
            AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
            at.concatenate(AffineTransform.getRotateInstance(angle));
            g2d.transform(at);

            // Draw horizontal arrow starting in (0, 0)
            g2d.drawLine(0, 0, len, 0);
            g2d.fillPolygon(new int[] {len, len-ARROW_SIZE, len-ARROW_SIZE, len},
                    new int[] {0, -ARROW_SIZE, ARROW_SIZE, 0}, 4);
            g2d.setTransform(transformBefore);
        }
    }
}
