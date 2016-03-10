package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Generator {
    void generate(Dispatch dispatch) throws IOException;
}
