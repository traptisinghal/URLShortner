package com.shorturl.messageQueueService;

import java.io.IOException;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorturl.exceptionhandling.CustomBaseException;
import com.shorturl.model.Click_Info;
import com.shorturl.repository.ClickInfoRepository;

@Component
public class RecordClickQueueMessageReceiverService {

	ClickInfoRepository clickInfoRepository;

	ObjectMapper objectMapper;

	public RecordClickQueueMessageReceiverService(ClickInfoRepository clickInfoRepository, ObjectMapper objectMapper) {
		super();
		this.clickInfoRepository = clickInfoRepository;
		this.objectMapper = objectMapper;
	}

	public static final String EXCHANGE = "link-exchange";
	public static final String QUEUE_NAME = "link-queue";

	@RabbitListener(bindings = @QueueBinding(value = @Queue(value = QUEUE_NAME, durable = "true"), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.TOPIC, durable = "true")))
	public void receiveMessage(String clickInfo_json) throws CustomBaseException {
		System.out.println("Queue Receiver invoked...");
		if (!clickInfo_json.isEmpty()) {
			System.out.println("click Info in Json Format in Queue receiver" + clickInfo_json);
			Click_Info clickInfo = null;
			try {
				// convert json to object
				clickInfo = objectMapper.readValue(clickInfo_json, Click_Info.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				throw new CustomBaseException("Exception occured while pushing data to Queue", e);
			}
			
			clickInfoRepository.save(clickInfo);
		}

	}

}
