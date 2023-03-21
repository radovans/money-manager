package cz.sinko.moneymanager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("SELECT t FROM Transaction t WHERE (LOWER(t.note) LIKE LOWER(:search) OR LOWER(t.recipient) LIKE LOWER(:search)) AND t.date BETWEEN :from AND :to")
	Page<Transaction> findByNoteLikeAndDateBetween(Pageable pageable, @Param("search") String search, @Param("from") LocalDate from, @Param("to") LocalDate to);

	@Query("SELECT t FROM Transaction t WHERE (LOWER(t.note) LIKE LOWER(:search) OR LOWER(t.recipient) LIKE LOWER(:search)) AND t.category.name = :category AND t.date BETWEEN :from AND :to")
	Page<Transaction> findByNoteLikeAndDateBetweenAndCategory(Pageable pageable, @Param("search") String search, @Param("from") LocalDate from, @Param("to") LocalDate to, @Param("category") String category);

	Page<Transaction> findByDateBetween(Pageable pageable, LocalDate from, LocalDate to);

	@Query("SELECT t FROM Transaction t WHERE t.category.name = :category AND t.date BETWEEN :from AND :to")
	Page<Transaction> findByDateBetweenAndCategory(Pageable pageable, LocalDate from, LocalDate to, String category);

	List<Transaction> findByDateBetween(LocalDate from, LocalDate to);

	@Query("SELECT t FROM Transaction t WHERE t.category.name = :category AND t.date BETWEEN :from AND :to")
	List<Transaction> findByDateBetweenAndCategory(LocalDate from, LocalDate to, String category);

	List<Transaction> findBySubcategory(Subcategory subcategory);
}
