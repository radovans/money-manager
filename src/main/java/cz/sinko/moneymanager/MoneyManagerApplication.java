package cz.sinko.moneymanager;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import cz.sinko.moneymanager.model.Pokemon;
import cz.sinko.moneymanager.model.PokemonList;

@SpringBootApplication
public class MoneyManagerApplication {

	@Bean
	ApplicationRunner applicationRunner(PokemonClient pokemonClient) {
		return args -> {
			System.out.println(pokemonClient.pokemons());
			System.out.println(pokemonClient.pokemon(156l));
		};
	}

	@Bean
	PokemonClient pokemonClient() {
		WebClient webClient = WebClient.builder()
				.baseUrl("https://pokeapi.co/api/v2")
				.build();
		HttpServiceProxyFactory factory = new HttpServiceProxyFactory(new WebClientAdapter(webClient));
		return factory.createClient(PokemonClient.class);
	}

	@HttpExchange("/pokemon")
	interface PokemonClient {

		@GetExchange
		PokemonList pokemons();

		@GetExchange("/{pokemonId}")
		Pokemon pokemon(@PathVariable("pokemonId") Long id);

	}

}

