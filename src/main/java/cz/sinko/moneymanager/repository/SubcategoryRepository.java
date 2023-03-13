package cz.sinko.moneymanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

	Optional<Subcategory> findByName(String category);

	List<Subcategory> findByCategory(Category category);
}
