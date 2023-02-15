package cz.sinko.moneymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.MainCategory;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {
	MainCategory findByName(String mainCategory);

}
