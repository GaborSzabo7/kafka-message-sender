package hu.gaszabo.kafka.message.sender.data;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.gaszabo.kafka.message.sender.util.UUIDGenerator;

public final class Data implements Serializable {

	private static final long serialVersionUID = -3472070773911955491L;

	private final String id;
	private final String payload;

	@JsonCreator
	public Data(@JsonProperty(value = "id") final String id, @JsonProperty(value = "payload") final String payload) {
		this.id = id;
		this.payload = payload;
	}

	public Data(final String payload) {
		id = UUIDGenerator.generate();
		this.payload = requireNonNull(payload, "payload must not be null");
	}

	public String getId() {
		return id;
	}

	public String getPayload() {
		return payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (payload == null ? 0 : payload.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Data other = (Data) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (payload == null) {
			if (other.payload != null) {
				return false;
			}
		} else if (!payload.equals(other.payload)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Data [id=" + id + ", payload=" + payload + "]";
	}

}
