package net.rubygrapefruit.ipc;

import java.io.IOException;

public interface Generator {
    void generate(Dispatch dispatch) throws IOException;
}
