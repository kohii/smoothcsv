package com.smoothcsv.commons.encoding;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Value;

/**
 * @author kohii
 */
@Value
public class FileEncoding {

  public static final FileEncoding UTF_8;
  public static final FileEncoding UTF_8_WITH_BOM;

  public static final FileEncoding UTF_16BE;
  public static final FileEncoding UTF_16LE;
  public static final FileEncoding UTF_16BE_WITH_BOM;
  public static final FileEncoding UTF_16LE_WITH_BOM;

  public static final FileEncoding UTF_32BE;
  public static final FileEncoding UTF_32LE;
  public static final FileEncoding UTF_32BE_WITH_BOM;
  public static final FileEncoding UTF_32LE_WITH_BOM;

  public static final FileEncoding MS932;
  public static final FileEncoding SHIFT_JIS;

  private static final List<FileEncoding> ALL;

  static {
    UTF_8 = new FileEncoding(StandardCharsets.UTF_8, false, "UTF-8", StandardCharsets.UTF_8.aliases());
    UTF_8_WITH_BOM = new FileEncoding(StandardCharsets.UTF_8, true, "UTF-8 (with BOM)", Collections.emptySet());

    Charset utf16be = Charset.forName("UTF-16BE");
    Charset utf16le = Charset.forName("UTF-16LE");
    Charset utf16beBom = StandardCharsets.UTF_16;
    Charset utf16leBom = Charset.forName("x-UTF-16LE-BOM");
    UTF_16BE = new FileEncoding(utf16be, false, "UTF-16BE", utf16be.aliases());
    UTF_16LE = new FileEncoding(utf16le, false, "UTF-16LE", utf16le.aliases());
    UTF_16BE_WITH_BOM = new FileEncoding(utf16beBom, false, "UTF-16BE (with BOM)", add(utf16beBom.aliases(), utf16beBom.displayName()));
    UTF_16LE_WITH_BOM = new FileEncoding(utf16leBom, false, "UTF-16LE (with BOM)", add(utf16leBom.aliases(), utf16leBom.displayName()));

    Charset utf32be = Charset.forName("UTF-32BE");
    Charset utf32le = Charset.forName("UTF-32LE");
    Charset utf32beBom = Charset.forName("x-UTF-32BE-BOM");
    Charset utf32leBom = Charset.forName("x-UTF-32LE-BOM");
    UTF_32BE = new FileEncoding(utf32be, false, "UTF-32BE", add(utf32beBom.aliases(), "UTF-32"));
    UTF_32LE = new FileEncoding(utf32le, false, "UTF-32LE", utf32le.aliases());
    UTF_32BE_WITH_BOM = new FileEncoding(utf32beBom, false, "UTF-32BE (with BOM)", add(utf32beBom.aliases(), utf32beBom.displayName()));
    UTF_32LE_WITH_BOM = new FileEncoding(utf32leBom, false, "UTF-32LE (with BOM)", add(utf32leBom.aliases(), utf32leBom.displayName()));

    Charset ms932 = Charset.forName("MS932");
    MS932 = new FileEncoding(ms932, false, "Shift_JIS (MS932)", new HashSet<>(Arrays.asList("MS932", "Windows-31J", "Windows-932")));

    Charset shiftJis = Charset.forName("Shift_JIS");
    Set<String> shiftJisAliases = new HashSet<>();
    shiftJisAliases.add("Shift_JIS");
    shiftJisAliases.addAll(shiftJis.aliases());
    SHIFT_JIS = new FileEncoding(shiftJis, false, "Shift_JIS (Original)", shiftJisAliases);

    List<FileEncoding> list = Charset.availableCharsets().entrySet()
        .stream()
        .filter(entry -> {
          String key = entry.getKey().toLowerCase();
          return !key.startsWith("utf") && !key.startsWith("x-utf");
        })
        .filter(entry -> !entry.getValue().equals(ms932) && !entry.getValue().equals(shiftJis))
        .map(entry -> {
          Charset c = entry.getValue();
          return new FileEncoding(c, false, c.displayName(), c.aliases());
        }).collect(Collectors.toList());

    list.add(UTF_8);
    list.add(UTF_8_WITH_BOM);
    list.add(UTF_16BE);
    list.add(UTF_16LE);
    list.add(UTF_16BE_WITH_BOM);
    list.add(UTF_16LE_WITH_BOM);
    list.add(UTF_32BE);
    list.add(UTF_32LE);
    list.add(UTF_32BE_WITH_BOM);
    list.add(UTF_32LE_WITH_BOM);
    list.add(MS932);
    list.add(SHIFT_JIS);

    list.sort(Comparator.comparing(FileEncoding::getName));
    ALL = Collections.unmodifiableList(list);
  }

  private static FileEncoding defaultFileEncoding = UTF_8;

  Charset charset;

  boolean hasUtf8BOM;

  String name;

  Set<String> aliases;

  public static Optional<FileEncoding> forName(String name) {
    return ALL.stream()
        .filter(enc -> enc.getName().equalsIgnoreCase(name) || enc.getName().contains(name))
        .findFirst();
  }

  public static Optional<FileEncoding> of(Charset charset, boolean hasUtf8BOM) {
    if (hasUtf8BOM) {
      if (charset.equals(StandardCharsets.UTF_8)) {
        return Optional.of(UTF_8_WITH_BOM);
      } else {
        throw new IllegalArgumentException();
      }
    }
    return of(charset);
  }

  public static Optional<FileEncoding> of(Charset charset) {
    return ALL.stream()
        .filter(enc -> enc.getCharset().equals(charset))
        .findFirst();
  }

  public static List<FileEncoding> getAvailableEncodings() {
    return ALL;
  }

  private static Set<String> add(Set<String> set, String elm) {
    Set<String> newSet = new HashSet<>(set);
    newSet.add(elm);
    return newSet;
  }

  public static FileEncoding getDefault() {
    return defaultFileEncoding;
  }

  public static void setDefault(FileEncoding encoding) {
    FileEncoding.defaultFileEncoding = encoding;
  }

  public boolean hasUtf8BOM() {
    return hasUtf8BOM;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FileEncoding that = (FileEncoding) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
