package hu.gaszabo.kafka.message.sender.data.rest;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.gaszabo.kafka.message.sender.data.service.DataMessageSender;

@RestController
@RequestMapping("/data")
public class DataRestController {

	private final DataMessageSender dataMessageSender;

	@Autowired
	public DataRestController(final DataMessageSender dataMessageSender) {
		this.dataMessageSender = requireNonNull(dataMessageSender, "dataMessageSender must not be null");
	}

	@PutMapping(value = "/send")
	public Callable<ResponseEntity<Void>> send(@RequestBody final String text) {
		return () -> {
			dataMessageSender.send(text);
			return new ResponseEntity<>(NO_CONTENT);
		};
	}

	@PutMapping(value = "/send/{multiplier}")
	public Callable<ResponseEntity<Void>> sendMultipleTimes( //
			@PathVariable final int multiplier, @RequestBody final String text) {
		return () -> {
			dataMessageSender.sendMultipleTimes(multiplier, text);
			return new ResponseEntity<>(NO_CONTENT);
		};
	}

}
