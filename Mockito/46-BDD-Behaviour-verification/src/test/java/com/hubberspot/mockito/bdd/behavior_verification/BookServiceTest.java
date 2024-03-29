package com.hubberspot.mockito.bdd.behavior_verification;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

	@InjectMocks
	private BookService bookService;

	@Mock
	private BookRepository bookRepository;

	// Traditional Mockito-style behavior verification
	@Test
	public void testUpdatePrice2() {
		// Given - Arrange
		Book book = new Book("1234", "Mockito In Action", 500, LocalDate.now());
		when(bookRepository.findBookById("1234")).thenReturn(book);

		// When - Act
		bookService.updatePrice("1234", 500);

		// Then - Assert/Verify
		verify(bookRepository).findBookById("1234");
		verify(bookRepository).save(book);
		verifyNoMoreInteractions(bookRepository);
	}

	// BDD-style behavior verification
	@Test
	public void test_Given_ABook_When_UpdatePriceIsCalledWithNewPrice_Then_BookPriceIsUpdated() {
		// Given - Arrange
		Book book = new Book("1234", "Mockito In Action", 500, LocalDate.now());
		given(bookRepository.findBookById("1234")).willReturn(book);

		// When - Act
		bookService.updatePrice("1234", 500);

		// Then - Assert/Verify using BDDMockito's then()
		then(bookRepository).should().save(book);
	}
}
