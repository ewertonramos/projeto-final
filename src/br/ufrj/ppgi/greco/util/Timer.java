package br.ufrj.ppgi.greco.util;

public class Timer {
	
	private long time;
	
	public Timer() {
		this.time = System.currentTimeMillis();
	}
	public void start(){
		this.time = System.currentTimeMillis();
	}
	public long getTime(){
		return System.currentTimeMillis() - this.time;
	}
}