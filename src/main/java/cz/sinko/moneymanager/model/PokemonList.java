package cz.sinko.moneymanager.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PokemonList {

	private int count;
	private List<Pokemon> results;

}
