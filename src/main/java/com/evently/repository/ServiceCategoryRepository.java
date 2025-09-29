package com.evently.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.ServiceCategory;


@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
    // Find category by name
    Optional<ServiceCategory> findByName(String name);
    
    Optional<ServiceCategory> findByNameIgnoreCase(String name);
    
    // Find categories by name pattern
    List<ServiceCategory> findByNameContainingIgnoreCase(String name);
    
    // Find categories with description
    List<ServiceCategory> findByDescriptionIsNotNull();
    
    // Find categories without description
    List<ServiceCategory> findByDescriptionIsNull();
    
    // Check if category exists by name
    boolean existsByName(String name);
    
    boolean existsByNameIgnoreCase(String name);
    
    // Custom queries
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.description LIKE %:keyword% OR sc.name LIKE %:keyword%")
    List<ServiceCategory> findByKeywordInNameOrDescription(@Param("keyword") String keyword);
    
    @Query("SELECT sc FROM ServiceCategory sc ORDER BY sc.name ASC")
    List<ServiceCategory> findAllOrderByName();
    
    // Find categories with services
    @Query("SELECT DISTINCT sc FROM ServiceCategory sc JOIN sc.services s")
    List<ServiceCategory> findCategoriesWithServices();
    
    // Find categories without services
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc NOT IN (SELECT DISTINCT s.category FROM Service s WHERE s.category IS NOT NULL)")
    List<ServiceCategory> findCategoriesWithoutServices();
    
    // Count services by category
    @Query("SELECT sc, COUNT(s) FROM ServiceCategory sc LEFT JOIN sc.services s GROUP BY sc")
    List<Object[]> countServicesByCategory();
    
    // Find popular categories (with most services)
    @Query("SELECT sc FROM ServiceCategory sc JOIN sc.services s GROUP BY sc ORDER BY COUNT(s) DESC")
    List<ServiceCategory> findPopularCategories();
}
