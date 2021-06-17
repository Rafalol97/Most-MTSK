package Utils;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandleValueMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InteractionToBeSend {
    private InteractionClassHandle interactionClassHandle;
    private ParameterHandleValueMap parameters;
}

