import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Tetris extends Canvas{
  int borderRight = playGroundWidth - cubeSize;
  int borderBottom = playGroundHeight - cubeSize;
  int lineCubeNum = playGroundWidth / cubeSize;
  static int winX = 660;
  static int winY = 700;
  static int playGroundOffset = 15;
  public boolean gameFlag = true;
  public ArrayList<BrickPoint> fillBricks = new ArrayList<BrickPoint>();
  public static int cubeSize = 40;
  public static int playGroundWidth = 400;
  public static int playGroundHeight = 640;
  public static Point currentBrickPosition = new Point(0, 0);
  public static BrickState currentBrickState = new BrickState(0, 0);
  public static Tetris tetris = new Tetris();
  public static BrickPoint bricks[][][] = new BrickPoint[7][4][16];
  static Score score = new Score();

  public static void main(String args[]){
    // Create frame, button and add event listener
    Frame frame = new Frame("Tetris");
    frame.setSize(winX, winY);

    tetris.addKeyListener(new BrickOperation());
    tetris.addMouseListener(new MouseOperation());
    frame.addWindowListener(new Window());

    // Create bricks
    tetris.createBricks();

    // Create canvas and button object(main function won't execute when it's an instance)
    frame.add(tetris, BorderLayout.CENTER);

    // Timer decline currentBrick
    TimerTask timerTask = new TimerTask(){
      public void run(){
        new Operation().processKeyInput(KeyEvent.VK_DOWN);
      }
    };
    new Timer().scheduleAtFixedRate(timerTask, 0, 500);

    frame.setVisible(true);
  }

  public void paint(Graphics g){
    if(gameFlag){
      drawPlayGround(g);
      renderCurrentBrick(g);
      renderFillBricks(g);
      showScore(g);
    }else{
      showGameOver(g);
    }
  }

  public void showGameOver(Graphics g){
    drawPlayGround(g);
    renderFillBricks(g);
    showScore(g);
    showTipMessage(g);
  }

  public void showScore(Graphics g){
    g.setColor(Color.black);
    g.setFont(new Font("Arial", Font.PLAIN, 30));
    g.drawString("Score", winX - 175, 150);
    g.drawString(String.valueOf(score.getScore()), winX - 150, 200);
  }

  public void showTipMessage(Graphics g){
    g.setColor(Color.black);
    g.setFont(new Font("Arial", Font.PLAIN, 15));
    g.drawString("Game Over, 新方塊沒地方放惹", winX - 205, 250);
    g.drawString("點一下螢幕重新開始", winX - 195, 290);
  }

  public void drawPlayGround(Graphics g){
    int x = 0;
    int y = 0;
    g.drawRect(playGroundOffset, playGroundOffset, playGroundWidth, playGroundHeight);
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
        {1, 2, 4, 5}, {0, 4, 5, 9}, {1, 2, 4, 5}, {0, 4, 5, 9}
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

  public void renderCurrentBrick(Graphics g){
    int brick = Tetris.currentBrickState.getBrick();
    int rotate = Tetris.currentBrickState.getRotate();
    BrickPoint target[] = bricks[brick][rotate];

    for(int i = 0 ; i < bricks[0][0].length ; i++){
      if(target[i].isRender){
        g.setColor(Color.blue);
        g.fillRect((int)target[i].getX() + playGroundOffset + 1 + (int)currentBrickPosition.getX(), (int)target[i].getY() + playGroundOffset + 1 + (int)currentBrickPosition.getY(), cubeSize - 1, cubeSize - 1);
      }
    }
  }

  public void renderFillBricks(Graphics g){
    for(int i = 0 ; i < fillBricks.size() ; i++){
        g.setColor(Color.red);
        g.fillRect((int)fillBricks.get(i).getX() + 1, (int)fillBricks.get(i).getY() + 1, cubeSize - 1, cubeSize - 1);
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

  public void updateFillBricks(){
    int brick = Tetris.currentBrickState.getBrick();
    int rotate = Tetris.currentBrickState.getRotate();
    BrickPoint target[] = bricks[brick][rotate];

    // Add fill bricks
    for(int i = 0 ; i < bricks[0][0].length ; i++){
      if(target[i].isRender){
        int fillX = (int)target[i].getX() + (int)currentBrickPosition.getX() + playGroundOffset;
        int fillY = (int)target[i].getY() + (int)currentBrickPosition.getY() + playGroundOffset;
        BrickPoint fillPoint = new BrickPoint(fillX, fillY, true);
        fillBricks.add(fillPoint);
      }
    }


    // Clean line if it's possible
    int cleanLineNum = 0;

    for(int i = 0 ; i < fillBricks.size() ; i++){
      if(fillBricks.get(i).isRender){
        // Check is there are avaliable clear line
        int nowFillNum = 0;
        for(int j = 0 ; j < fillBricks.size() ; j++){
          if(fillBricks.get(i).getY() == fillBricks.get(j).getY()){
            nowFillNum++;
          }
        }

        double targetY = fillBricks.get(i).getY();  // Find specify Y coordinate

        if(nowFillNum % lineCubeNum == 0){
          cleanLineNum++;

          // Mark specify Y coordinate filled line
          for(int r = 0 ; r < fillBricks.size() ; r++){
            if(fillBricks.get(r).getY() == targetY){
              fillBricks.get(r).turnOff();
            }

            // Above specify Y's point will decline
            if(fillBricks.get(r).getY() < targetY){
              fillBricks.get(r).move((int)fillBricks.get(r).getX(), (int)fillBricks.get(r).getY() + cubeSize);
            }
          }
        }
      }
    }

    // Score add cleanLineNum
    score.addScore(cleanLineNum);

    // Update fillBricks, remove clean line's brick
    ArrayList<BrickPoint> updateFillBricks = new ArrayList<BrickPoint>();
    for(int i = 0 ; i < fillBricks.size() ; i++){
      if(fillBricks.get(i).isRender){
        updateFillBricks.add(fillBricks.get(i));
      }
    }

    fillBricks = new ArrayList<BrickPoint>(updateFillBricks);

    currentBrickState.nextBrick();
    currentBrickPosition.move(0, 0);
  }

  public void cleanFillBricks(){
    fillBricks = new ArrayList<BrickPoint>();
  }

  public void gameOver(){
    gameFlag = false;
  }

  public void gameStart(){
    cleanFillBricks();
    score = new Score();
    currentBrickState = new BrickState(0, 0);
    currentBrickPosition.move(0, 0);
    gameFlag = true;
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

class BrickOperation extends KeyAdapter{
  int cubeSize;

  public BrickOperation(){
    this.cubeSize = Tetris.cubeSize;
  }

  public void keyPressed(KeyEvent e){
    int keyCode = e.getKeyCode();
    processKeyInput(keyCode);
  }

  public void processKeyInput(int keyCode){
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
      case KeyEvent.VK_SPACE:
        moveToButtom();
        break;
    }

    if(checkBound()){  // When out of bound return true
      processBrick(keyCode);
    }

    if(checkGameOver()){
      Tetris.tetris.gameOver();
    }

    Tetris.tetris.repaint();
  }

  public void rotate(){
    Tetris.currentBrickState.nextRotate();
  }

  public void moveDown(){
    Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX(), (int)Tetris.currentBrickPosition.getY() + this.cubeSize);
  }

  public void moveLeft(){
    Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX() - this.cubeSize, (int)Tetris.currentBrickPosition.getY());
  }

  public void moveRight(){
    Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX() + this.cubeSize, (int)Tetris.currentBrickPosition.getY());
  }

  public void moveToButtom(){
    int targetDistance = 0;
    int destinationY = Tetris.playGroundHeight;
    int brick = Tetris.currentBrickState.getBrick();
    int rotate = Tetris.currentBrickState.getRotate();
    int currentX = (int)Tetris.currentBrickPosition.getX();
    int currentY = (int)Tetris.currentBrickPosition.getY();
    BrickPoint target[] = Tetris.bricks[brick][rotate];
    ArrayList<BrickPoint> fillBricks = new ArrayList<BrickPoint>(Tetris.tetris.fillBricks);

    // Find the each current X's bottom Y
    ArrayList<Point> topPoints = new ArrayList<Point>();
    for(int i = 0 ; i < target.length ; i++){
      if(target[i].isRender){
        double nowX = target[i].getX();
        int bottomY = (int)target[i].getY();

        for(int j = 0 ; j < target.length ; j++){
          if(target[j].isRender && target[j].getX() == nowX){
            if(bottomY < target[j].getY()){
              bottomY = (int)target[j].getY();
            }
          }
        }

        Point topPoint = new Point((int)nowX + Tetris.playGroundOffset + currentX, bottomY + currentY);
        if( !(topPoints.contains(topPoint)) ){
          topPoints.add(topPoint);
        }
      }
    }

    // Find the each corresponding X with topPoints in fillBricks's top Y
    ArrayList<Point> bottomPoints = new ArrayList<Point>();
    for(int i = 0 ; i < fillBricks.size() ; i++){
      double targetX = fillBricks.get(i).getX();
      double bottomY = fillBricks.get(i).getY();

      // Check now point's x is match one of topPoints
      boolean checkFlag = false;
      for(int j = 0 ; j < topPoints.size() ; j++){
        double topPointX = topPoints.get(j).getX();
        if(targetX == topPointX){
          checkFlag = true;
          break;
        }
      }

      // Find the top Y with nowX
      if(checkFlag){
        for(int r = 0 ; r < fillBricks.size() ; r++){
          double nowX = fillBricks.get(r).getX();
          double nowY = fillBricks.get(r).getY();
          if(nowX == targetX){
            if(bottomY > nowY){
              bottomY = nowY;
            }
          }
        }

        Point bottomPoint = new Point((int)targetX, (int)bottomY);
        if( !(bottomPoints.contains(bottomPoint)) ){
          bottomPoints.add(bottomPoint);
        }
      }
    }

    // Find all distance
    int distance[] = new int[topPoints.size()];
    for(int i = 0 ; i < topPoints.size() ; i++){
      double topX = topPoints.get(i).getX();
      double topY = topPoints.get(i).getY();
      for(int j = 0 ; j < bottomPoints.size() ; j++){
        double bottomX = bottomPoints.get(j).getX();
        double bottomY = bottomPoints.get(j).getY();
        if(topX == bottomX){
          distance[i] = (int)bottomY - (int)topY - Tetris.playGroundOffset - this.cubeSize;
        }
      }
    }

    // If there's no brick beneath the current bricks, then use bottom topPoint and Tetris.playGroundHeight to count
    for(int i = 0 ; i < topPoints.size() ; i++){
      if(distance[i] == 0){
        double bottomY = topPoints.get(0).getY();
        for(int j = 0 ; j < topPoints.size() ; j++){
          if(bottomY < topPoints.get(j).getY()){
            bottomY = topPoints.get(j).getY();
          }
        }
        distance[i] = Tetris.playGroundHeight - (int)bottomY - this.cubeSize;
      }
    }

    // Find min distance, and that's the correct length
    targetDistance = distance[0];
    for(int i = 0 ; i < topPoints.size() ; i++){
      int nowDistance = distance[i];
      if(targetDistance > nowDistance){
        targetDistance = nowDistance;
      }
    }

    Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX(), (int)Tetris.currentBrickPosition.getY() + targetDistance);
    this.processKeyInput(KeyEvent.VK_DOWN);
  }

  public boolean checkBound(){
    boolean result = false;
    int brick = Tetris.currentBrickState.getBrick();
    int rotate = Tetris.currentBrickState.getRotate();
    int currentX = (int)Tetris.currentBrickPosition.getX();
    int currentY = (int)Tetris.currentBrickPosition.getY();
    int leftBound = 0 + Tetris.playGroundOffset;
    int rightBound = leftBound + Tetris.playGroundWidth;
    int bottomBound = Tetris.playGroundHeight + Tetris.playGroundOffset;
    BrickPoint target[] = Tetris.bricks[brick][rotate];
    ArrayList<BrickPoint> fillBricks = new ArrayList<BrickPoint>(Tetris.tetris.fillBricks);

    for(int i = 0 ; i < Tetris.bricks[0][0].length ; i++){
      if(target[i].isRender){
        int nowFillX = (int)target[i].getX() + currentX + Tetris.playGroundOffset;
        int nowFillY = (int)target[i].getY() + currentY + Tetris.playGroundOffset;

        // Check left and right bound
        if(nowFillX < leftBound){
          result = true;
          break;
        }

        if(nowFillX >= rightBound){
          result = true;
          break;
        }

        if(nowFillY >= bottomBound){
          result = true;
          break;
        }else{
          if( !(fillBricks.isEmpty()) ){
            for(int j = 0 ; j < fillBricks.size() ; j++){
              int filledX = (int)fillBricks.get(j).getX();
              int filledY = (int)fillBricks.get(j).getY();

              if(nowFillY == filledY){
                if(nowFillX == filledX){
                  result = true;
                  break;
                }
              }
            }
          }
        }

      }
    }

    return result;
  }

  public void processBrick(int lastAction){
    switch(lastAction){
      case KeyEvent.VK_UP:
        Tetris.currentBrickState.backRotate();
        break;
      case KeyEvent.VK_DOWN:
        Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX(), (int)Tetris.currentBrickPosition.getY() - this.cubeSize);
        Tetris.tetris.updateFillBricks();
        break;
      case KeyEvent.VK_LEFT:
        Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX() + this.cubeSize, (int)Tetris.currentBrickPosition.getY());
        break;
      case KeyEvent.VK_RIGHT:
        Tetris.currentBrickPosition.move((int)Tetris.currentBrickPosition.getX() - this.cubeSize, (int)Tetris.currentBrickPosition.getY());
        break;
    }
  }

  public boolean checkGameOver(){
    boolean result = false;
    int brick = Tetris.currentBrickState.getBrick();
    int rotate = Tetris.currentBrickState.getRotate();
    BrickPoint target[] = Tetris.bricks[brick][rotate];
    ArrayList<BrickPoint> fillBricks = new ArrayList<BrickPoint>(Tetris.tetris.fillBricks);

    for(int i = 0 ; i < fillBricks.size() ; i++){
      BrickPoint fillBrick = fillBricks.get(i);

      for(int j = 0 ; j < target.length ; j++){
        if(target[j].isRender){
          if(target[j].getX() + Tetris.playGroundOffset == fillBrick.getX()){
            if(target[j].getY() + Tetris.playGroundOffset == fillBrick.getY()){
              result = true;
              break;
            }
          }
        }
      }

    }

    return result;

  }
}

class BrickState extends Point{
  public BrickState(int brickNum, int rotateNum){
    super(brickNum, rotateNum);
  }

  public int getBrick(){
    return (int)this.getX();
  }

  public int getRotate(){
    return (int)this.getY();
  }

  public void nextBrick(){
    this.move((this.getBrick() + 1) % 7, this.getRotate());
  }

  public void nextRotate(){
    this.move(this.getBrick(), (this.getRotate() + 1) % 4);
  }

  public void backRotate(){
    this.move(this.getBrick(), (this.getRotate() + 4 - 1) % 4);
  }
}

class Score{
  private int score;

  public Score(){
    this.score = 0;
  }

  public void addScore(int score){
    this.score += score;
  }

  public int getScore(){
    return this.score;
  }
}

class MouseOperation extends MouseAdapter{
  public void mouseClicked(MouseEvent e) {
    Tetris.tetris.gameStart();
  }
}
