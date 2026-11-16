package dev.reliefgrid.api;
import dev.reliefgrid.model.Category; import jakarta.validation.constraints.*;
public class Dto {
 public record RequestCreate(@NotNull Long shelterId,@NotNull Category category,@Min(1) int quantity,@Min(1) @Max(5) int urgency){}
 public record StockCreate(@NotNull Long shelterId,@NotNull Category category,@Min(0) int available){}
 public record VolunteerCreate(@NotBlank String displayName,@NotNull Long shelterId,@Min(0) int capacity){}
 public record SyncItem(@NotBlank String key,@NotNull RequestCreate request){}
 public record SyncResult(String key,String status,Long requestId,String message){}
 public record ApiError(String error,String message){}
}
