package dev.lavalink.youtube.cipher;

import org.jetbrains.annotations.NotNull;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Describes one signature cipher
 */
public class SignatureCipher {
  public final String timestamp;
  public final String rawScript;

  public SignatureCipher(@NotNull String timestamp,
                         @NotNull String rawScript) {
    this.timestamp = timestamp;
    this.rawScript = rawScript;
  }

  /**
   * @param text         Text to transform
   * @param functionName Name of the function to execute
   * @param scriptEngine JavaScript engine to execute function
   * @return The result of the n parameter transformation
   */
  public String transform(@NotNull String text, @NotNull String functionName, @NotNull ScriptEngine scriptEngine)
      throws ScriptException, NoSuchMethodException {
    String transformed;

    scriptEngine.eval(rawScript);
    transformed = (String) ((Invocable) scriptEngine).invokeFunction(functionName, text);

    return transformed;
  }

}
