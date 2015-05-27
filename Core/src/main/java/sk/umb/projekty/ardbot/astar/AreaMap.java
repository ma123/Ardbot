package sk.umb.projekty.ardbot.astar;

import java.util.ArrayList;

public class AreaMap {
	  private int mapWidth;
      private int mapHeight;
      private ArrayList<ArrayList<Node>> map;
      private int startLocationX = 0;
      private int startLocationY = 0;
      private int goalLocationX = 0;
      private int goalLocationY = 0;
      private int[][] obstacleMap;
      
      AreaMap(int mapWidth, int mapHeight, int[][] obstacleMap) {
              this.mapWidth = mapWidth;
              this.mapHeight = mapHeight;
              this.obstacleMap = obstacleMap;
              
              createMap();
              registerEdges();
      }
      private void createMap() {
              Node node;
              map = new ArrayList<ArrayList<Node>>();
              for (int x=0; x<mapWidth; x++) {
                      map.add(new ArrayList<Node>());
                      for (int y=0; y<mapHeight; y++) {
                              node = new Node(x,y);
                              if (obstacleMap[x][y] == 1)
                                      node.setObstical(true);
                              map.get(x).add(node);
                      }
              }
      }

      /**
       * Registers the nodes edges (connections to its neighbors).
       */
      private void registerEdges() {
              for ( int x = 0; x < mapWidth-1; x++ ) {
                      for ( int y = 0; y < mapHeight-1; y++ ) {
                              Node node = map.get(x).get(y);
                              if (!(y==0))
                                      node.setNorth(map.get(x).get(y-1));
                              if (!(y==0) && !(x==mapWidth))
                                      node.setNorthEast(map.get(x+1).get(y-1));
                              if (!(x==mapWidth))
                                      node.setEast(map.get(x+1).get(y));
                              if (!(x==mapWidth) && !(y==mapHeight))
                                      node.setSouthEast(map.get(x+1).get(y+1));
                              if (!(y==mapHeight))
                                      node.setSouth(map.get(x).get(y+1));
                              if (!(x==0) && !(y==mapHeight))
                                      node.setSouthWest(map.get(x-1).get(y+1));
                              if (!(x==0))
                                      node.setWest(map.get(x-1).get(y));
                              if (!(x==0) && !(y==0))
                                      node.setNorthWest(map.get(x-1).get(y-1));
                      }
              }
      }
      
      

      public ArrayList<ArrayList<Node>> getNodes() {
              return map;
      }
      public void setObstical(int x, int y, boolean isObstical) {
              map.get(x).get(y).setObstical(isObstical);
      }

      public Node getNode(int x, int y) {
              return map.get(x).get(y);
      }

      public void setStartLocation(int x, int y) {
              map.get(startLocationX).get(startLocationY).setStart(false);
              map.get(x).get(y).setStart(true);
              startLocationX = x;
              startLocationY = y;
      }

      public void setGoalLocation(int x, int y) {
              map.get(goalLocationX).get(goalLocationY).setGoal(false);
              map.get(x).get(y).setGoal(true);
              goalLocationX = x;
              goalLocationY = y;
      }

      public int getStartLocationX() {
              return startLocationX;
      }

      public int getStartLocationY() {
              return startLocationY;
      }
      
      public Node getStartNode() {
              return map.get(startLocationX).get(startLocationY);
      }

      public int getGoalLocationX() {
              return goalLocationX;
      }

      public int getGoalLocationY() {
              return goalLocationY;
      }
      
      public Node getGoalLocation() {
              return map.get(goalLocationX).get(goalLocationY);
      }
      
      public float getDistanceBetween(Node node1, Node node2) {
              //if the nodes are on top or next to each other, return 1
              if (node1.getX() == node2.getX() || node1.getY() == node2.getY()){
                      return 1*(mapHeight+mapWidth);
              } else { //if they are diagonal to each other return diagonal distance: sqrt(1^2+1^2)
                      return (float) 1.7*(mapHeight+mapWidth);
              }
      }
      
      public int getMapWith() {
              return mapWidth;
      }
      public int getMapHeight() {
              return mapHeight;
      }
      public void clear() {
              startLocationX = 0;
              startLocationY = 0;
              goalLocationX = 0;
              goalLocationY = 0;
              createMap();
              registerEdges();
      }
}