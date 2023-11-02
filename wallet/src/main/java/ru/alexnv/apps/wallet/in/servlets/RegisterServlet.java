package ru.alexnv.apps.wallet.in.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import ru.alexnv.apps.wallet.aop.annotations.Loggable;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.validators.PlayerDtoValidator;
import ru.alexnv.apps.wallet.in.Utility;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

/**
 * Сервлет регистрации игрока.
 */
@Loggable
public class RegisterServlet extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** Вспомогательный класс. */
	private Utility util;
	
	/** Вспомогательный класс для HTTP сервлетов. */
	private ServletsUtility servletsUtil;
	
    /**
     * Создание сервлета регистрации игрока.
     *
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
    	util = new Utility();
    	servletsUtil = new ServletsUtility();
    }

	/**
	 * Регистрация игрока
	 * Входной JSON преобразуется в DTO
	 * Вызывается сервис регистрации игрока с полями из DTO
	 * При успешной регистрации возвращается статус код SC_CREATED (200)
	 * При ошибке регистрации возвращается статус код SC_CONFLICT (209).
	 *
	 * @param request запрос
	 * @param response ответ
	 * @throws ServletException ошибка сервлета
	 * @throws IOException ошибка ввода-вывода
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PlayerDto playerDto = null;
		int responseCode = SC_BAD_REQUEST;
		
		try {
			playerDto = servletsUtil.readValidDto(request, PlayerDto.class, new PlayerDtoValidator());
		} catch (IOException e) {
			servletsUtil.respondWithError(response, responseCode, "Ошибка парсинга JSON.");
			return;
		} catch (DtoValidationException e) {
			servletsUtil.respondWithErrors(response, responseCode, e.getViolations());
			return;
		}
		
		responseCode = SC_CONFLICT;
		// Выполнение операции для DTO
		var playerService = (PlayerService) getServletContext().getAttribute("PlayerService");
		try {
			String registeredLogin = playerService.registration(playerDto.getLogin(), playerDto.getPassword());
			responseCode = SC_CREATED;
			util.logMessage("Пользователь " + registeredLogin + " зарегистрирован.");
		} catch (RegistrationException e) {
			e.printStackTrace();
		}
		
		// Отправка итогового DTO клиенту в JSON
		servletsUtil.respondWithJson(response, responseCode, playerDto);
	}
	
}
