
/*
 *  Copyright (C) 2020 Dustin K. Redmond
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

import com.dustinredmond.BCrypt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Dustin K. Redmond
 * @since 03/26/2020 14:40
 */
public class BCryptGeneratorTest {

    /**
     * Basic sanity test to check the BCrypt algorithm.
     */
    @Test
    public void testBCrypt() {
        String plainText = "abc12345";
        String hashedPass = BCrypt.hashpw(plainText, BCrypt.gensalt());
        assertTrue(BCrypt.checkpw(plainText, hashedPass));
    }
}
