/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.swabunga.spell.engine;

/**
 * The various settings used to control how a spell checker works are read from here.
 * Includes the COST_* constants that decide how to figure the cost of converting one word to
 * another in the EditDistance class.
 * <p/>
 * Also includes SPELL_* constants that control how misspellings are detected, for example, how to handle
 * mixed-case words, etc.
 *
 * @author aim4min
 * @see EditDistance
 */
public abstract class Configuration {

  /** used by EditDistance: the cost of having to remove a character <br/>(integer greater than 0) */
  public static int COST_REMOVE_CHAR = 95;

  /** used by EditDistance: the cost of having to insert a character <br/>(integer greater than 0)*/
  public static int COST_INSERT_CHAR = 95;

  /**
   * used by EditDistance: the cost of having to swap two adjoining characters
   * for the swap value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values
   * <br/>(integer greater than 0)
   */
  public static int COST_SWAP_CHARS = 100;

  /**
   * used by EditDistance: the cost of having to change case, for example, from i to I.
   * <br/>(integer greater than 0)
   */
  public static int COST_CHANGE_CASE = 10;

  /**
   * used by EditDistance: the cost of having to substitute one character for another
   * for the sub value to ever be used, it should be smaller than the COST_REMOVE_CHAR or COST_INSERT_CHAR values
   * <br/>(integer greater than 0)
   */
  public static int COST_SUBST_CHARS = 100;

//    public static final String EDIT_SIMILAR = "EDIT_SIMILAR"; //DMV: these does not seem to be used at all
//    public static final String EDIT_MIN = "EDIT_MIN";
//    public static final String EDIT_MAX = "EDIT_MAX";

  /** the maximum cost of suggested spelling. Any suggestions that cost more are thrown away
   * <br/> integer greater than 1)
   */
  public static int SPELL_THRESHOLD = 140;

  /** words that are all upper case are not spell checked, example: "CIA" <br/>(boolean) */
  public static boolean SPELL_IGNOREUPPERCASE = true;
  /**  words that have mixed case are not spell checked, example: "SpellChecker"<br/>(boolean) */
  public static boolean SPELL_IGNOREMIXEDCASE = false;
  /** words that look like an Internet address are not spell checked, example: "http://www.google.com" <br/>(boolean)*/
  public static boolean SPELL_IGNOREINTERNETADDRESSES = true;
  /** words that have digits in them are not spell checked, example: "mach5" <br/>(boolean) */
  public static boolean SPELL_IGNOREDIGITWORDS = true;
  /** I don't know what this does. It doesn't seem to be used <br/>(boolean) */
  public static boolean SPELL_IGNOREMULTIPLEWORDS = false;
  /** the first word of a sentence is expected to start with an upper case letter <br/>(boolean) */
  public static boolean SPELL_IGNORESENTENCECAPITALIZATION = true;
}