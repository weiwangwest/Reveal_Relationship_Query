public class Timer{
public static long startTime, lastTime;
	public static long start(String message){
		startTime=System.currentTimeMillis();
		lastTime=startTime;
		if (message!=null){			
			System.out.println(message==null?"":message );
			System.out.println("start at " + new java.util.Date());
		}
		return startTime;
	}
	public static long stop(String message){
		long totalTime=lastTime-startTime;
		tick(message);
		if (message!=null){
			System.out.println("Total time:" + (totalTime) + "  milli-seconds.");
			System.out.println("end at " + new java.util.Date());			
		}
		return totalTime;
	}
	public static long tick(String message){
		long currentTime=System.currentTimeMillis();
		long duration=currentTime-lastTime;
		if (message!=null){
			System.out.println((duration) + " milli-seconds." );
			System.out.println();
			System.out.println("---------------------" +message+"---------------------");
		}
		lastTime=currentTime;
		return duration;
	}
	public static void main(String[] args) {
		Timer.start("start");
		Timer.tick("stage1");
		for (long i=0; i<1000000; i++){
			for (long j=0; i<1000000; i++){
				for (long k=0; i<1000000; i++){
					for (long l=0; i<1000000; i++){						
						for (long m=0; i<1000000; i++){		
							System.out.print("");
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
							System.out.print("");
						}
					}
					
				}
			}
		}
		Timer.stop("finished here");
	}
}
