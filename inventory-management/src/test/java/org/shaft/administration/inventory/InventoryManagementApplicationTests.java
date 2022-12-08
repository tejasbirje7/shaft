package org.shaft.administration.inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.shaft.administration.inventory.entity.orders.Item;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class InventoryManagementApplicationTests {

	@Test
	void contextLoads() {
	}


	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper m = new ObjectMapper();
		String value = "{\"oid\":1668005296,\"items\":[{\"id\":\"VGVuZGVyIENvY29udXQ=\",\"costPrice\":150,\"quantity\":1,\"option\":\"xs\"},{\"id\":\"Q3VzdGFyZCBBcHBsZSBQdWxw\",\"costPrice\":150,\"quantity\":1,\"option\":\"xs\"},{\"id\":\"R3JhdGVkIGNvY29udXQ=\",\"costPrice\":150,\"quantity\":1,\"option\":\"xs\"},{\"id\":\"U3BpbmFjaA==\",\"costPrice\":150,\"quantity\":1,\"option\":\"xs\"},{\"id\":\"UGVlbGVkIEdhcmxpYw==\",\"costPrice\":40,\"quantity\":1,\"option\":\"xs\"},{\"id\":\"UG9tZWdyYW5hdGU=\",\"costPrice\":70,\"quantity\":3,\"option\":\"xs\"}]}";
		Map<String,Object> req = m.readValue(value,Map.class);
		List<Item> items= m.convertValue(req.get("items"), new TypeReference<List<Item>>() {});
		List<String> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
		itemIds.forEach(System.out::println);

	}

}
