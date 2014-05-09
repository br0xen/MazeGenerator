import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Maze {
  Map<String, MazeBuilder.MazeObject> grid = new HashMap<String, MazeBuilder.MazeObject>();
  public static void main(String[] args) {
    int height, width;
    height = 25;
    width = 25;
    MazeBuilder mb = new MazeBuilder(width, height);
    Map<String, MazeBuilder.MazeObject> grid = mb.generate();
    for(int y = 0; y < height; y++) {
      for(int x = 0; x < width; x++) {
        if(grid.get(x+";"+y) instanceof MazeBuilder.MazeWall
            && !((MazeBuilder.MazeWall)grid.get(x+";"+y)).open) {
          System.out.print("#");
        } else {
          if(x == mb.startX && y == mb.startY) {
            System.out.print("S");
          } else if(x == mb.endX && y == mb.endY) {
            System.out.print("E");
          } else {
            System.out.print(" ");
          }
        }
      }
      System.out.print("\n");
    }
  }

  public static class MazeBuilder {
    Map<String, MazeObject> grid = new HashMap<String, MazeObject>();
    Stack<MazeCell> mazeStack = new Stack<MazeCell>();
    Random r;
    int width, height,
        startX, startY,
        endX, endY;
    boolean foundEnd = false;
    public MazeBuilder(int w, int h) {
      this(w, h, new Random());
    }
    public MazeBuilder(int w, int h, Random rand) {
      // Both height and width need to be odd
      width = w;
      height = h;
      r = rand;
    }

    public Map<String, MazeObject> generate() {
      // Build 'em
      // Build outter walls
      for(int out = 0; out <= height; out++) {
        grid.put("0;"+out, new MazeWall(0,out));
        grid.put(out+";0", new MazeWall(out,0));
        grid.put(width+";"+out, new MazeWall(width,out));
        grid.put(out+";"+height, new MazeWall(out,height));
      }
      for(int y = 1; y < height; y++) {
        for(int x = 1; x < width; x++) {
          if(((x)%2 == 0) || ((y)%2 == 0)) {
            grid.put(x+";"+y, new MazeWall(x, y));
          } else {
            grid.put(x+";"+y, new MazeCell(x, y));
          }
        }
      }
      // Link 'em
      for(int y = 0; y < height; y++) {
        for(int x = 0; x < width; x++) {
          if(x > 1) {
            grid.get(x+";"+y).setWest(grid.get((x-1)+";"+(y)));
          }
          if(x < width) {
            grid.get(x+";"+y).setEast(grid.get((x+1)+";"+(y)));
          }
          if(y > 1) {
            grid.get(x+";"+y).setNorth(grid.get((x)+";"+(y-1)));
          }
          if(y < height) {
            grid.get(x+";"+y).setSouth(grid.get((x)+";"+(y+1)));
          }
        }
      }
      // Start Building
      startX = r.nextInt((width-1))+1;
      while((startX)%2 == 0) {
        startX = r.nextInt((width-1))+1;
      }
      startY = r.nextInt((height-1))+1;
      while((startY)%2 == 0) {
        startY = r.nextInt((height-1))+1;
      }
      mazeStack.push(new MazeCell(startX, startY));
      buildMaze();
      return grid;
    }

    public void buildMaze() {
      MazeCell wrk2 = mazeStack.peek();
      MazeCell wrk = (MazeCell)grid.get(wrk2.x+";"+wrk2.y);
      wrk.visited = true;
      int startDir = r.nextInt(4);
      switch(startDir) {
        case 0:
          // Starting north
          tryToMoveNorth(wrk);
          tryToMoveEast(wrk);
          tryToMoveSouth(wrk);
          tryToMoveWest(wrk);
          break;
        case 1:
          // Starting east
          tryToMoveEast(wrk);
          tryToMoveSouth(wrk);
          tryToMoveWest(wrk);
          tryToMoveNorth(wrk);
          break;
        case 2:
          // Starting south
          tryToMoveSouth(wrk);
          tryToMoveWest(wrk);
          tryToMoveNorth(wrk);
          tryToMoveEast(wrk);
          break;
        case 3:
          // Starting west
          tryToMoveWest(wrk);
          tryToMoveNorth(wrk);
          tryToMoveEast(wrk);
          tryToMoveSouth(wrk);
          break;
      }
      // Should be done with this cell now
      MazeCell p = mazeStack.pop();
      if(!foundEnd) {
        endX = p.x;
        endY = p.y;
        foundEnd = true;
      }
    }

    public void tryToMoveNorth(MazeCell current) {
      if(current.getNorthCell() != null && !current.getNorthCell().visited) {
        // Break down the north wall
        ((MazeWall)current.getNorth()).open = true;
        mazeStack.push(current.getNorthCell());
        buildMaze();
      }
    }
    public void tryToMoveEast(MazeCell current) {
      if(current.getEastCell() != null && !current.getEastCell().visited) {
        // Break down the east wall
        ((MazeWall)current.getEast()).open = true;
        mazeStack.push(current.getEastCell());
        buildMaze();
      }
    }
    public void tryToMoveSouth(MazeCell current) {
      if(current.getSouthCell() != null && !current.getSouthCell().visited) {
        // Break down the east wall
        ((MazeWall)current.getSouth()).open = true;
        mazeStack.push(current.getSouthCell());
        buildMaze();
      }
    }
    public void tryToMoveWest(MazeCell current) {
      if(current.getWestCell() != null && !current.getWestCell().visited) {
        // Break down the east wall
        ((MazeWall)current.getWest()).open = true;
        mazeStack.push(current.getWestCell());
        buildMaze();
      }
    }

    public class MazeObject {
      int x, y;
      MazeObject north, east, south, west;
      public MazeObject(int x, int y) {
        this.x = x;
        this.y = y;
      }

      public void setNorth(MazeObject n) {
        if(n == null) { return; }
        this.north = n;
        n.south = this;
      }
      public void setEast(MazeObject e) {
        if(e == null) { return; }
        this.east = e;
        e.west = this;
      }
      public void setSouth(MazeObject s) {
        if(s == null) { return; }
        this.south = s;
        s.north = this;
      }
      public void setWest(MazeObject w) {
        if(w == null) { return; }
        this.west = w;
        w.east = this;
      }

      public MazeObject getNorth() {
        if(this.north == null) { return null; }
        return this.north;
      }
      public MazeCell getNorthCell() {
        if(this.north == null) { return null; }
        if(!(this.north instanceof MazeCell)) {
          return (MazeCell)this.north.getNorthCell();
        }
        return (MazeCell)this.north;
      }

      public MazeObject getEast() {
        if(this.east == null) { return null; }
        return this.east;
      }
      public MazeCell getEastCell() {
        if(this.east == null) { return null; }
        if(!(this.east instanceof MazeCell)) {
          return (MazeCell)this.east.getEastCell();
        }
        return (MazeCell)this.east;
      }

      public MazeObject getSouth() {
        if(this.south == null) { return null; }
        return this.south;
      }
      public MazeCell getSouthCell() {
        if(this.south == null) { return null; }
        if(!(this.south instanceof MazeCell)) {
          return (MazeCell)this.south.getSouthCell();
        }
        return (MazeCell)this.south;
      }

      public MazeObject getWest() {
        if(this.west == null) { return null; }
        return this.west;
      }
      public MazeCell getWestCell() {
        if(this.west == null) { return null; }
        if(!(this.west instanceof MazeCell)) {
          return (MazeCell)this.west.getWestCell();
        }
        return (MazeCell)this.west;
      }
    }

    public class MazeWall extends MazeObject {
      public boolean open = false;
      public MazeWall(int x, int y) { super(x, y); }
    }

    public class MazeCell extends MazeObject {
      public boolean visited = false;
      public MazeCell(int x, int y) { super(x, y); }
    }
  }
}