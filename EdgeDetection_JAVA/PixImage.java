import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.*;


	public class PixImage extends BufferedImage{
		private int width;
		private int height;
	    private BufferedImage image;  
	
		
		public PixImage(){
			super(0,0,0);
			
		}
		
		public PixImage(int width,int height){
			super(width, height, BufferedImage.TYPE_BYTE_BINARY);
			this.width=width;
			this.height=height;
		}
		
		public int getWidth(){
			return width;
		}
		
		public int getHeight(){
			return height;
		}

		public int[][] sobelEdges(BufferedImage img) throws IOException{			
		    image=img;
		    
		    //get each pixel's red value,green value,blue value.
		    //And save them in a two-dimensional array separately. 
			short[][] getRed=new short [width][height];
			short[][] getBlue=new short [width][height];
			short[][] getGreen=new short [width][height];
		
		     	for(int x=0;x<width;x++){
				for(int y=0;y<height;y++){
					getRed[x][y]=(short)((image.getRGB(x,y) & 0x00ff0000) >> 16);
					getBlue[x][y]=(short) ((image.getRGB(x,y) & 0x000000ff));
					getGreen[x][y]=(short)((image.getRGB(x, y) & 0x0000ff00) >> 8);	
				
				}
			}
			
			//compute an approximate gradient (gx,gy) for each of the three colors. 
		    //And save them in a two-dimensional array separately.
		  
			long[][]redGradient=getGradient(getRed);
			long[][]greenGradient=getGradient(getGreen);
			long[][]blueGradient=getGradient(getBlue);
			
			//convert every pixel's energy value in the range 0...255.	
			long[][] red=trimEnergy(redGradient,getMax(redGradient),getMin(redGradient));//red value in range 0~255;
			long[][] green=trimEnergy(greenGradient,getMax(greenGradient),getMin(greenGradient));
			long[][] blue=trimEnergy(blueGradient,getMax(blueGradient),getMin(blueGradient));
			
			//get final energy value for each pixel;
			int[][] energy=new int[width-2][height-2];
			int threshold=128;
	            for(int i=0;i<image.getWidth()-2;i++){
					for(int j=0;j<image.getHeight()-2;j++){
						//the energy of a pixel is the sum of its red, green, and blue energies
					    energy[i][j]=(int) (red[i][j]+green[i][j]+blue[i][j]);
					    /*The image can use a threshold value to make a binary image with 
					      values of only 0 or 255 to better present the edges.*/
                        if(energy[i][j]>=threshold)
					    	energy[i][j]=255;
					    if(energy[i][j]<threshold)
					    	energy[i][j]=0;
					
					}
				}
			
			return energy;
		}
		
		
		
		
		public void setPixel() throws IOException{
			int [][] gray=sobelEdges(image);
			//create a new PixImage
			PixImage newImage = new PixImage(image.getWidth(), image.getHeight());
			
			//set RGB,the output is a grayscale image. 
			for(int i=0;i<newImage.getWidth()-2;i++){
				for(int j=0;j<newImage.getHeight()-2;j++){
                    Color color=new Color(gray[i][j],gray[i][j],gray[i][j]);
					newImage.setRGB(i, j, color.getRGB()); 
				}
			}
			//output a new image
			File output = new File("d:/testPixImage.jpg");
			ImageIO.write((RenderedImage)newImage, "jpg", output);

		}
		

		

		private long[][] getGradient(short[][] color){
			int sobel_x[][] = {{1,0,-1},
		                      {2,0,-2},
		                      {1,0,-1}};
		    int sobel_y[][] = {{1,2,1},
		                       {0,0,0},
		                      {-1,-2,-1}};
		    int gx;
		    int gy;
		    int width=color.length;
		    int height=color[0].length;
		    long[][]energy=new long[width-2][height-2];
		    
		    for(int i=1;i<width-1;i++){
				for(int j=1;j<height-1;j++){
				gx=sobel_x[0][0]*color[i-1][j-1]+sobel_x[0][1]*color[i][j-1]+sobel_x[0][2]*color[i+1][j-1]
					+sobel_x[1][0]*color[i-1][j]+sobel_x[1][1]*color[i][j]+sobel_x[1][2]*color[i+1][j]
					+sobel_x[2][0]*color[i-1][j+1]+sobel_x[2][1]*color[i][j+1]+sobel_x[2][2]*color[i+1][j+1];
					
				gy=sobel_y[0][0]*color[i-1][j-1]+sobel_y[0][1]*color[i][j-1]+sobel_y[0][2]*color[i+1][j-1]
					+sobel_y[1][0]*color[i-1][j]+sobel_y[1][1]*color[i][j]+sobel_y[1][2]*color[i+1][j]
					+sobel_y[2][0]*color[i-1][j+1]+sobel_y[2][1]*color[i][j+1]+sobel_y[2][2]*color[i+1][j+1];
				energy[i-1][j-1]=(long)(Math.sqrt(gx*gx+gy*gy));
				}
		    }
		    return energy;
		}
		
		private long getMax(long[][] list){
			long max=Long.MIN_VALUE;
			for(int i=0;i<list.length;i++){
				for(int j=0;j<list[i].length;j++){
					if(max<list[i][j])
						max=list[i][j];
				}
			}
			return max;
		}
		
		private long getMin(long[][] list){
			long min=Long.MAX_VALUE;
			for(int i=0;i<list.length;i++){
				for(int j=0;j<list[i].length;j++){
					if(min>list[i][j])
						min=list[i][j];
				}
			}
			return min;
		}
		
		//convert every pixel's energy in the range 0...255.
		private long[][] trimEnergy(long[][]energy,long max,long min){
			
			for(int i=0;i<energy.length;i++){
				for(int j=0;j<energy[i].length;j++){
					energy[i][j]=(255*(energy[i][j]-min))/(max-min);
				}	
			}	
			return energy;
		}
		
	}
	
