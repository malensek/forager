/*
Copyright (c) 2015, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package forager.ui;

import java.io.IOException;

/**
 * Defines an interface for commands in the Forager CLI. Commands are
 * responsible for parsing arguments and setting up whatever objects they need
 * to execute their task.
 *
 * @author malensek
 */
interface Command {

    /**
     * Applies the command line arguments provided, and executes the command.
     */
    public int execute(String[] args) throws Exception;

    /**
     * Retrieves the name of the command. This string allow users to call a
     * specific command by name on the CLI.
     */
    public String name();

    /**
     * A short (one line) description of the Command for contextual help
     * purposes.
     */
    public String description();

    /**
     * Prints usage information for this command on stdout.
     */
    public void printUsage() throws IOException;
}
