package application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BatchUnparkRequest {
    private final List<Integer> slotNumbers;

    public BatchUnparkRequest(List<Integer> slotNumbers) {
        this.slotNumbers = slotNumbers == null ? new ArrayList<>() : new ArrayList<>(slotNumbers);
    }

    public List<Integer> getSlotNumbers() {
        return Collections.unmodifiableList(slotNumbers);
    }
}