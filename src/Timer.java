public class Timer{
public static long startTime, lastTime;
	public static void start(String message){
		startTime=System.nanoTime();
		lastTime=startTime;
		System.out.println(message==null?"":message );
		System.out.println("start at " + new java.util.Date());
	}
	public static void stop(String message){
		tick(message);
		System.out.println("Total time:" + (lastTime-startTime)/1000/1000 + " seconds.");
		System.out.println("end at " + new java.util.Date());
	}
	public static void tick(String message){
		long currentTime=System.nanoTime();
		System.out.println((currentTime-lastTime)/1000/1000 + " seconds." );
		System.out.println();
		System.out.println("---------------------" +message+"---------------------");
		lastTime=currentTime;
	}
	public static void main(String[] args) {
		Timer.start("start");
		Timer.tick("stage1");
		for (long i=0; i<1000000; i++){
			for (long j=0; i<1000000; i++){
				for (long k=0; i<1000000; i++){
					for (long l=0; i<1000000; i++){						
						for (long m=0; i<1000000; i++){						
						}
					}					
				}
			}
		}
		Timer.tick("stage2");
		for (long i=0; i<1000000; i++){
			for (long j=0; i<1000000; i++){
				for (long k=0; i<1000000; i++){
					for (long l=0; i<1000000; i++){						
						for (long m=0; i<1000000; i++){						
						}
					}
					
				}
			}
		}
		Timer.stop("finied here");
	}
}
