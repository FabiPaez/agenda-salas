    package com.agendasalas.controller;

    import com.agendasalas.dto.ErrorResponse;
    import com.agendasalas.exception.ConflictoException;
    import com.agendasalas.exception.RecursoNoEncontradoException;

    import org.springframework.dao.DataIntegrityViolationException;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.time.LocalDateTime;

    @RestControllerAdvice
    public class ApiExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> manejarValidaciones(
                MethodArgumentNotValidException ex) {

            String mensaje = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage())
                    .orElse("Los datos enviados no son válidos");

            return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
        }

        @ExceptionHandler(RecursoNoEncontradoException.class)
        public ResponseEntity<ErrorResponse> manejarNoEncontrado(
                RecursoNoEncontradoException ex) {

            return construirRespuesta(
                    HttpStatus.NOT_FOUND,
                    ex.getMessage()
            );
        }

        @ExceptionHandler(ConflictoException.class)
        public ResponseEntity<ErrorResponse> manejarConflictos(
                ConflictoException ex) {

            return construirRespuesta(
                    HttpStatus.CONFLICT,
                    ex.getMessage()
            );
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> manejarIntegridad(
                DataIntegrityViolationException ex) {

            return construirRespuesta(
                    HttpStatus.CONFLICT,
                    "La operación genera un conflicto con los datos existentes"
            );
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> manejarDatosInvalidos(
                IllegalArgumentException ex) {

            return construirRespuesta(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage()
            );
        }

        private ResponseEntity<ErrorResponse> construirRespuesta(
                HttpStatus estado,
                String mensaje) {

            ErrorResponse respuesta = new ErrorResponse(
                    estado.value(),
                    mensaje,
                    LocalDateTime.now()
            );

            return ResponseEntity
                    .status(estado)
                    .body(respuesta);
        }
    }