package Utilities;

import hla.rti1516e.InteractionClassHandle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InteractionWithType {
    private InteractionClassHandle interaction;
    private boolean published, subscribed;
}
