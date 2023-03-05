package cz.sinko.moneymanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.MainCategory;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByName(String category);

	List<Category> findByMainCategory(MainCategory mainCategory);
}
