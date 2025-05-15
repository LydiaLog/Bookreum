package com.bookreum.dev.domain.book;
//5. AladinBookService.java (Service Layer)

import  com.bookreum.dev.domain.book.AladinBookClient;
import  com.bookreum.dev.domain.book.AladinSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AladinBookService {

 private final AladinBookClient aladinBookClient;

 public AladinSearchResult searchBooks(String keyword) {
     return aladinBookClient.searchBooks(keyword);
 }
}