package com.bookreum.dev.domain.book;

import java.time.LocalDate;

import com.bookreum.dev.category.CategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book")
public class BookEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long aladinId;

    @Column(unique = true)
    private String isbn13;

    private String title;
    private String originalTitle;
    private String subtitle;
    private String author;
    private String publisher;

    private LocalDate pubdate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    @Column(columnDefinition = "TEXT")
    private String fullDescription2;

    private String link;
    private String coverImageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    private LocalDate createdAt = LocalDate.now();
	
	
}
