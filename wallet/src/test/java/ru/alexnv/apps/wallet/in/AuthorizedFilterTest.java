/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.security.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.in.filters.*;

/**
 * 
 */
class AuthorizedFilterTest {

	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	void setup() {
		// Создаём mock объекты запроса и ответа
		this.request = mock(HttpServletRequest.class);
		this.response = mock(HttpServletResponse.class);
	}
	
	@Test
	final void should_returnNothing_when_PostContentTypeIsJson() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(chain, times(1)).doFilter(request, response);
	}
	
	@Test
	final void should_returnRespondWith403_when_TokenFirstPartIncorrect() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer 123hbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(403, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith403_when_TokenSignatureIncorrect() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.TAMPERED_SIGNATURE");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(403, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_IdURINotEqualsTokenPayloadSubId() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/3/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_IDNotNumber() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/two/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_IDNotSet() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("//balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_IDTypeDouble() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2.00/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_URIEndsWithNotBalance() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance/add");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_URIHasSeveralIDs() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_AuthorizationHeaderMissing() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance/");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_returnRespondWith401_when_AuthorizationHeaderNotStartWithBearer() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance/add");
		when(request.getHeader("Authorization")).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_throwInvalidKeyException_whenJWTValidateError()
			throws IOException, ServletException, InvalidKeyException, NoSuchAlgorithmException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");
		JSONWebToken jwt = mock(JSONWebToken.class);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = spy(AuthorizedFilter.class);
		
		doThrow(InvalidKeyException.class).when(jwt).isValidToken(any());
		when(authorizedFilter.createJWT()).thenReturn(jwt);

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);
		
		Executable executable = () -> jwt.isValidToken(any());
		
		// Проверяем, что бросилось исключение
		assertThrows(InvalidKeyException.class, executable);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}
	
	@Test
	final void should_throwNoSuchAlgorithmException_whenJWTValidateError()
			throws IOException, ServletException, InvalidKeyException, NoSuchAlgorithmException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");
		JSONWebToken jwt = mock(JSONWebToken.class);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = spy(AuthorizedFilter.class);
		
		doThrow(NoSuchAlgorithmException.class).when(jwt).isValidToken(any());
		when(authorizedFilter.createJWT()).thenReturn(jwt);

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);
		
		Executable executable = () -> jwt.isValidToken(any());
		
		// Проверяем, что бросилось исключение
		assertThrows(NoSuchAlgorithmException.class, executable);

		// Проверяем выполнение фильтра
		verify(response, times(1)).sendError(401, "Invalid access token");
	}

}
