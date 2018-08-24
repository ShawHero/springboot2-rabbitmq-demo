package cn.itshaw.rebbitmqdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RebbitmqDemoApplicationTests {

	@Autowired
	Producer producer;

	@Test
	public void testSendMessage(){
		System.out.println("testSendMessage:start");
		for(int i = 0 ; i<1000; i++){
			producer.sendMessage("hello world");
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("testSendMessage:end");
	}

}
