import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

class Tetris extends Canvas{
  static int cubeSize = 40;
  static int winX = 620;
  static int winY = 700;
  static int playGroundX = 400;
  static int playGroundY = 640;
  static int playGroundOffset = 15;
  public static Point currentPosition = new Point(0, 0);
  public static Tetris tetris = new Tetris();
  public static Operation operation = new Operation(cubeSize);
  int borderRight = playGroundX - cubeSize;
  int borderBottom = playGroundY - cubeSize;
  BrickPoint bricks[][][] = new BrickPoint[7][4][16];

  public static void main(String args[]){
    // Create frame and add some event listener
    Frame frame = new Frame("Tetris");
    frame.setSize(winX, winY);
    frame.addWindowListener(new Window());
    tetris.addKeyListener(operation);

    // Create bricks
    tetris.createBricks();

    // Create canvas object(main function won't execute when it's an instance)
    frame.add(tetris, BorderLayout.CENTER);

    frame.setVisible(true);
  }

  public void paint(Graphics g){
    drawPlayGround(g);
    test(g);
  }

  public void drawPlayGround(Graphics g){
    int x = 0;
    int y = 0;
    g.drawRect(playGroundOffset, playGroundOffset, playGroundX, playGroundY);
    while(y <= borderBottom){
      x = 0;
      while(x <= borderRight){
        g.drawRect(x + playGroundOffset, y + playGroundOffset, cubeSize, cubeSize);
        x += cubeSize;
      }
      y += cubeSize;
    }
  }

  public void createBricks(){
    // Cube order include rotated
    int order[][][] =  {
      // Brick I
      {
        {4, 5, 6, 7}, {2, 6, 10, 14}, {4, 5, 6, 7}, {2, 6, 10, 14}
      },

      // Brick J
      {
        {4, 8, 9, 10}, {0, 1, 4, 8}, {0, 1, 2, 6}, {2, 6, 9, 10}
      },

      // Brick L
      {
        {6, 10, 8, 9}, {0, 4, 8, 9}, {0, 1, 2, 4}, {1, 2, 6, 10}
      },

      // Brick O
      {
        {0, 1, 4, 5}, {0, 1, 4, 5}, {0, 1, 4, 5}, {0, 1, 4, 5}
      },

      // Brick S
      {
        {5, 6, 8, 9}, {0, 4, 5, 9}, {5, 6, 8, 9}, {0, 4, 5, 9}
      },

      // Brick T
      {
        {5, 8, 9, 10}, {0, 4, 5, 8}, {0, 1, 2, 5}, {2, 5, 6, 10}
      },

      // Brick Z
      {
        {4, 5, 9, 10}, {2, 5, 6, 9}, {4, 5, 9, 10}, {2, 5, 6, 9}
      }
    };

    // Generate bricks, use brick to generate bricks
    BrickPoint brick[] = new BrickPoint[16];
    for(int i = 0 ; i < 4 ; i++){
      for(int j = 0 ; j < 4 ; j++){
        brick[i * 4 + j] = new BrickPoint(j * cubeSize, i * cubeSize, false);
      }
    }

    for(int i = 0 ; i < 7 ; i++){
      for(int j = 0 ; j < 4 ; j++){
        modifyBrickPoint(brick, order[i][j]);
        for(int r = 0 ; r < brick.length ; r++){
          bricks[i][j][r] = new BrickPoint((int)brick[r].getX(), (int)brick[r].getY(), brick[r].isRender);
        }
      }
    }
  }

  public void test(Graphics g){
    // Render cube
    BrickPoint temp[] = bricks[0][1];

    for(int i = 0 ; i < bricks[0][0].length ; i++){
      if(temp[i].isRender){
        g.setColor(Color.blue);
        g.fillRect((int)temp[i].getX() + playGroundOffset + 1 + (int)currentPosition.getX(), (int)temp[i].getY() + playGroundOffset + 1 + (int)currentPosition.getY(), cubeSize - 1, cubeSize - 1);
      }
    }
  }

  public void modifyBrickPoint(BrickPoint brick[], int openPoint[]){
    for(int i = 0 ; i < brick.length ; i++){
      for(int j = 0 ; j < openPoint.length ; j++){
        if(i == openPoint[j]){
          brick[i].turnOn();
          break;
        }else{
          brick[i].turnOff();
        }
      }
    }
  }

}

class Window extends WindowAdapter{
  public void windowClosing(WindowEvent e){
    System.exit(0);
  }
}

class BrickPoint extends Point{
  boolean isRender = false;

  public BrickPoint(int x, int y, boolean isRender){
    super(x, y);
    this.isRender = isRender;
  }

  public void turnOn(){
    this.isRender = true;
  }

  public void turnOff(){
    this.isRender = false;
  }
}

class Operation extends KeyAdapter{
  int offset;

  public Operation(int offset){
    this.offset = offset;
  }

  public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();

    switch(keyCode){
      case KeyEvent.VK_UP:
        rotate();
        break;
      case KeyEvent.VK_DOWN:
        moveDown();
        break;
      case KeyEvent.VK_LEFT:
        moveLeft();
        break;
      case KeyEvent.VK_RIGHT:
        moveRight();
        break;
    }

    Tetris.tetris.repaint();
  }

  public void rotate(){
    // to-do
  }

  public void moveDown(){
    Tetris.currentPosition.move((int)Tetris.currentPosition.getX(), (int)Tetris.currentPosition.getY() + this.offset);
  }

  public void moveLeft(){
    Tetris.currentPosition.move((int)Tetris.currentPosition.getX() - this.offset, (int)Tetris.currentPosition.getY());
  }

  public void moveRight(){
    Tetris.currentPosition.move((int)Tetris.currentPosition.getX() + this.offset, (int)Tetris.currentPosition.getY());
  }
}
