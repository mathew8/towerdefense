package project;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.border.*;
import java.util.*;

class Tower extends Canvas implements ActionListener, MouseListener
{
	String name;
    Graphics2D g2;
    double radian;
    int x = 165;
    int y = 165;
	int ptX0;
	int ptY0;
	int positionI;
	int positionJ;
    int mouseX;
    int mouseY;
	int attact;
	int range;
	int speed;
	int upgrade;
	Unit targetUnit;
	String attactType;
	int cost;
	Vector <Land>searchLands;
	JPanel inforPan;
	JButton buyBtn, cancelBtn;
	JButton upgradeBtn, sellBtn;
	JPanel informationPan;
	boolean build; // 건설되었나?
	String information;
	GameManager gm;
	javax.swing.Timer atk; // 공격 시간
	Potan potan;
			
    Tower(String n, int a, int r, int s, String atype, int c, String i, GameManager g)
    {
		name = n;
		attact = a;
		range = r; 
		speed = s;
		attactType = atype;
		cost = c;
		information = i;
		gm = g;
		potan = new Potan();
		atk = new javax.swing.Timer(speed, this); //공격 시간당 속도
		searchLands = new Vector<Land>();

        g2 = (Graphics2D)super.getGraphics();
		inforPan = new JPanel();
		informationPan = new JPanel();
		launchInforPan();
        setBounds(150,150,30,30);
    }

	public void launchInforPan()
	{
		inforPan.removeAll();
		inforPan.setBorder(new TitledBorder(""));
		inforPan.setLayout(new BoxLayout(inforPan, BoxLayout.Y_AXIS));
		inforPan.setAlignmentX(inforPan.LEFT_ALIGNMENT ); // 왼쪽정렬
		inforPan.add(new JLabel("이름 : " + name));
		inforPan.add(new JLabel("공격력 : " + String.valueOf(attact)));
		inforPan.add(new JLabel("사정거리 : " + String.valueOf(range)));
		inforPan.add(new JLabel("속도 : " + String.valueOf(speed)));		
		inforPan.add(new JLabel("공격타입 : " + attactType));
		inforPan.add(new JLabel("등 급 : " + String.valueOf(upgrade)));
		inforPan.add(new JLabel("가격  : " + String.valueOf(cost)));
		inforPan.add(new JLabel("정보 : "+ information));
		if (build == false) // 빌드가 안되어있을 시
		{
			
			buyBtn = new JButton("구 입");
			buyBtn.addActionListener(this);
			inforPan.add(buyBtn);
		}
		else // (build == true)
		{
			sellBtn = new JButton("팔 기");
			inforPan.add(sellBtn);
			sellBtn.addActionListener(this);
			inforPan.add(new JLabel("---*업그레이드*---"));
			inforPan.add(new JLabel("공격력 : " + String.valueOf(attact * 3)));
			inforPan.add(new JLabel("비 용 : " + String.valueOf(cost *2 + cost/2)));
			upgradeBtn = new JButton("업그래이드");
			inforPan.add(upgradeBtn);
			upgradeBtn.addActionListener( this);
		}
		inforPan.repaint();
	}

	
	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() == buyBtn)
		{
			gm.buyTower(name, cost);			
		}
		else if (ev.getSource() == upgradeBtn)
		{
			if (upgrade < 6) //업그레이드는 5 단계까지
			{
				gm.upgrade(this, cost *2 + cost/2); 
			}
			
		}
		else if(ev.getSource() == sellBtn)
		{
			atk.stop();
			gm.sellTower(this, cost - cost / 3);
		}
		if (targetUnit != null)
		{
			//포탄 potan = new 포탄(ptX0, ptY0);
			//ptVec.add(potan);
			potan.shootting();
		}
	}

	public String getAttactType(){
		return attactType;
	}

	public void paint(Graphics g)
    {
    }

	public void observe(int sero, int garo, Land land[][])
	{
		build = true;

		int startI = sero - range;  //정찰 위치(가로, 세로 좌표)
		if (startI < 0)
			startI = 0;
		int endI = sero + range;
		if (endI > 9)
			endI = 9;
		int startJ = garo - range;
		if (startJ < 0)
			startJ = 0;
		int endJ = garo + range;
		if (endJ > 14)
			endJ = 14;
		int count = 1;
		System.out.println(startI + " " + endI);
		System.out.println(startJ + " " + endJ);
		for (int i = startI; i < endI+1 ; i++  )
		{
			for (int j = startJ; j < endJ+1 ; j++ )
			{
				//System.out.println(i + " " + j);
				if (land[i][j].getType() == 1)
				{
					//land[i][j].setMouseIn(true);
					searchLands.add(land[i][j]);
					land[i][j].addTower(this);
					//System.out.println("land[" + i + "][" + j + "]가 정찰영역에 포함되었습니다.");
				}
			}
		}
	}

	public void setXY(int x, int y , int pI, int pJ)
	{
		this.x = x;
		this.y = y;
		this.ptX0 = x + 10;
		this.ptY0 = y + 10;
		positionI = pI;
		positionJ = pJ;
	}
	public int getPositionI(){
		return positionI;
	}
	public int getPositionJ(){
		return positionJ;
	}
	public void 사정거리계산(Unit u) //사정거리 
	{
		if (targetUnit == null)
		{
			targetUnit = u; 
			atk.start();     //공격 시작
		}
		else if (targetUnit == u)
		{
			turn(u.getX(), u.getY());
		}
		else
		{}
	}

	public void lostUnit(Unit u) //유닛이 없어질 때
	{
		if (u == targetUnit)
		{
			targetUnit = null;  //공격.stop();
		}
	}

	public void deadTarget(Unit u)
	{
		if (targetUnit == u)
		{
			//for (int i = 0; i < ptVec.size() ; i++ )
			//	gm.boom(ptVec.get(i));
			targetUnit = null;
			//ptVec.clear();
			atk.stop(); //공격 멈춤
		}
	}
	
	public void upgrade() //업그레이드
	{
		attact = attact * 3;
		cost = (cost *2 + cost/2) + cost / 2;
		launchInforPan();
		gm.showInfor(inforPan);
		upgrade++;
		repaint();
	}

    public void turn(int mx, int my) // 각도변경
    {
        mouseX = mx;
        mouseY = my;
        g2.rotate(-1*radian);
        repaint();
        radian = Math.atan2(mouseY - y, mouseX - x);
        g2.rotate(radian);
        repaint();
    }

	public void mousePressed (MouseEvent  e)
    {
		launchInforPan();
		gm.showInfor(inforPan);	
		GameManager.select = e.getComponent();
		gm.repaintAllTower();
	}
    public void mouseClicked (MouseEvent  e) {}// 
    public void mouseEntered (MouseEvent  e){}// 
    public void mouseExited (MouseEvent  e){}// 
    public void mouseReleased (MouseEvent  e){}	

		
	public Potan getPotan(){
		return potan;
	}
	
	class Potan extends Canvas implements ActionListener
	{
		Image img;
		boolean boom;
		javax.swing.Timer timer;
		int ptX;
		int ptY;
		int targetX;
		int targetY;
		String upDownX;
		String upDownY;
			
		Potan()
		{
			timer = new javax.swing.Timer(80, this);
			setBounds(ptX,ptY,5,5);
			setVisible(false);
		}

		public void setXY(int x, int y)
		{
			ptX = x;
			ptY = y;
		}
	
		public void paint(Graphics g)
		{
			if (name.equals("기본타워"))
				g.setColor(Color.black);
			else if (name.equals("파이어타워"))
				g.setColor(Color.red);
			else if (name.equals("아이스타워")){
				g.setColor(Color.black);
				g.fillRect(0,0,5,5);
				g.setColor(Color.white);
			}
			else if (name.equals("에어타워"))				
				g.setColor(Color.blue);
			else if (name.equals("독타워")){
				g.setColor(Color.black);
				g.fillRect(0,0,5,5);
				g.setColor(Color.green);
			}
			g.fillOval(0,0,5,5);			
		}

		public void shootting()
		{
			if (targetUnit != null)
			{			
				setBounds(ptX0,ptY0,5,5);
				ptX = ptX0;
				ptY = ptY0;
				setVisible(true);
				targetX = targetUnit.getX();
				targetY = targetUnit.getY();
				if (targetX > ptX)
					upDownX = "up";
				else
					upDownX = "down";
				if (targetY > ptY)
					upDownY = "up";
				else
					upDownY = "down";
				timer.start();
			}
		}
		
		public void actionPerformed(ActionEvent ev)
		{
			boolean ptXStop = false;
			boolean ptYStop = false;
			if (targetUnit == null)
			{
				timer.stop();
				setVisible(false);
			}
			else
			{
				if ("up".equals(upDownX))
				{
					ptX += 20;
					if (targetX < ptX){
						ptX = targetUnit.getX();
						ptXStop = true;
					}
				}
				else if ("down".equals(upDownX))
				{
					ptX -= 20;
					if (targetX > ptX){
						ptX = targetUnit.getX();
						ptXStop = true;
					}
				}

				if ("up".equals(upDownY))
				{
					ptY += 20;
					if (targetY < ptY){
						ptY = targetUnit.getY();
						ptYStop = true;
					}
				}
				else if ("down".equals(upDownY))
				{
					ptY -= 20;
					if (targetY > ptY){
						ptY = targetUnit.getY();
						ptYStop = true;
					}
				}

				
				setBounds(ptX, ptY, 5,5);
	
				if (ptXStop && ptYStop)
				{
					//System.out.println("명중");
					명중();
					setVisible(false);
					timer.stop();
					ptX = ptX0;
					ptY = ptY0;
				}
			}
		}
	}

	public void 명중()
	{
		targetUnit.damage(attact , Color.black, false);
	}
}

class NomalTower extends Tower
{
	Image img;
	NomalTower(GameManager gm)
	{
		super("기본타워",15, 1, 800,"지상+공중",20, "초반용 타워입니다.", gm);
		Toolkit toolit = Toolkit.getDefaultToolkit();
		img = toolit.getImage("./이미지/풀1.gif");
	}

	public void paint(Graphics g)
    {
        if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(15,15);            
        }
		g.drawImage(img,0,0,30,30,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.yellow);
			g.fillRect(1,1,28,28);			
		}
				
		g.setColor(Color.black);
		g.drawRect(1,1,28,28);
		g.drawLine(1,1,28,28);
		g.drawLine(28,1,1,28);
		
		g.setColor(Color.orange);
		for (int i = 0 ; i < upgrade ; i++ )
			g.fillRect(2,26 - i*5,10,4);

		g2.setColor(Color.black);
		g2.drawRect(0,-2,14,4);
        g2.fillOval(-6,-6,12,12);			
    }
	public void 명중()
	{
		targetUnit.damage(attact , Color.black , false);
	}
}

class FireTower extends Tower
{
	Image img;
	FireTower(GameManager gm)
	{// Tower(String n, int a, int r, int s, String atype, int c, String i)
		super("파이어타워", 25, 1, 1000, "지상", 100, "범위공격을합니다.", gm);
		Toolkit toolit = Toolkit.getDefaultToolkit();
		img = toolit.getImage("./이미지/풀1.gif");
	}

	public void paint(Graphics g)
    {
        if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(15,15);            
        }
		g.drawImage(img,0,0,30,30,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.yellow);
			g.fillRect(1,1,28,28);
		}
		g.setColor(Color.black);
		g.drawRect(1,1,28,28);
		g.drawLine(1,1,28,28);
		g.drawLine(28,1,1,28);
				
		g.setColor(Color.orange);
		for (int i = 0 ; i < upgrade ; i++ )
			g.fillRect(2,26 - i*5,10,4);

		g2.setColor(Color.red);
		//g2.fillRect(-15,-15,30,30);
        //g2.setColor(Color.black);
        g2.drawRect(0,-3,10,6);
        g2.fillRect(-4,-7,8,14);		
    }

	public void 명중()
	{
		targetUnit.damage(attact ,  Color.red , true);
	}
}

class IceTower extends Tower
{
	Image img;
	IceTower(GameManager gm)
	{
		super("아이스타워", 10, 2, 800, "지상+공중", 200, "범위공격및 프로즌타워", gm);
		Toolkit toolit = Toolkit.getDefaultToolkit();
		img = toolit.getImage("./이미지/풀1.gif");
	}

	public void paint(Graphics g)
    {
        if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(15,15);
        }
		g.drawImage(img,0,0,30,30,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.blue); // 아이스타워 선택했을 때 파랑색배경
			g.fillRect(1,1,28,28); //아이스타워 사각형 크기
		}
		g.setColor(Color.black);//이미지 대각선 검정색
		g.drawRect(1,1,28,28);
		g.drawLine(1,1,28,28);
		g.drawLine(28,1,1,28);
		
		g.setColor(Color.orange);
		for (int i = 0 ; i < upgrade ; i++ )
			g.fillRect(2,26 - i*5,10,4);

		g2.setColor(Color.white);
        g2.fillRect(0,-2,14,4);
        g2.fillOval(-6,-6,12,12);
		g2.drawLine(0,-6,14,-2);
		g2.drawLine(0,6,14,2);		
    }

	public void 명중()
	{
		targetUnit.damage(attact , Color.white , true); //타겟이 명중 되었을 때 흰 색으로 됨
	}
}

class AirTower extends Tower  //타워 클래스 안에 상속되는 에어타워 클래스를 만든다(에어 타워 정보)
{
	Image img;
	AirTower(GameManager gm)
	{
		super("에어타워", 50, 2, 800, "공중", 250, "범위공격입니다.", gm);	//에어타워의 정보
		Toolkit toolit = Toolkit.getDefaultToolkit(); //
		img = toolit.getImage("./이미지/풀1.gif"); // 배경이 풀이미지
	}

	public void paint(Graphics g)
    {
        if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(15,15);            
        }
		g.drawImage(img,0,0,30,30,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.yellow); //에어타워 선택했을 때 노란색 배경
			g.fillRect(1,1,28,28); //
		}
		g.setColor(Color.black); //이미지 대각선 검정색
		g.drawRect(1,1,28,28);
		g.drawLine(1,1,28,28);
		g.drawLine(28,1,1,28);
		
		g.setColor(Color.orange);
		for (int i = 0 ; i < upgrade ; i++ ) // 업그레이드 됬을 때 주황색 컬러 작은 사각형 표시
			g.fillRect(2,26 - i*5,10,4); //5단계 업그레이드 됨

		g2.setColor(Color.blue);// 에어타워 아이콘
		g2.fillRect(-6,-6,6,12);
		g2.fillRect(0,-4,12,3);
		g2.fillRect(0,1,12,3);			
    }

	public void 명중()
	{
		targetUnit.damage(attact , Color.blue, true); //타겟이 명중 되었을 때 파란색으로 됨
	}
}

class PoisonTower extends Tower
{
	Image img;
	PoisonTower(GameManager gm)
	{
		super("독타워",20, 3, 1000, "지상+공중", 150, "적을 중독시킵니다.", gm);	//독타워의 정보
		Toolkit toolit = Toolkit.getDefaultToolkit();
		img = toolit.getImage("./이미지/풀1.gif"); // 풀 이미지에만 넣을 수 있다. 
	}

	public void paint(Graphics g)
    {
        if(g2 == null)
        {
            g2 = (Graphics2D)getGraphics();
            g2.translate(15,15);            
        }
		g.drawImage(img,0,0,30,30,this);
		if (GameManager.select == this)
		{
			g.setColor(Color.yellow);//독타워 선택했을 때 노란색 배경
			g.fillRect(1,1,28,28);
		}
		g.setColor(Color.black);
		g.drawRect(1,1,28,28);
		g.drawLine(1,1,28,28);
		g.drawLine(28,1,1,28);
		
		g.setColor(Color.orange);
		for (int i = 0 ; i < upgrade ; i++ ) // 업그레이드 됬을 때 주황색 컬러 작은 사각형 표시
			g.fillRect(2,26 - i*5,10,4); // 5단계 업그레이드 됨

		g2.setColor(Color.black); //이미지 대각선 검정색
		g2.drawRect(0,-6,12,3);
		g2.drawRect(0,3,12,3);
		g2.setColor(Color.green);// 독타워 초록색 아이콘
		g2.fillOval(-6,-6,12,12);		
    }
	
	public void 명중()
	{
		targetUnit.damage(attact, Color.green, false);
	}
}