/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.shaft.administration.kafka.avro.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class EventData extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 8834501677751410598L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"EventData\",\"namespace\":\"org.shaft.administration.kafka.avro.model\",\"fields\":[{\"name\":\"eid\",\"type\":\"long\"},{\"name\":\"quantity\",\"type\":\"long\",\"default\":-1},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"costPrice\",\"type\":\"long\",\"default\":-1},{\"name\":\"onSale\",\"type\":\"boolean\",\"default\":false},{\"name\":\"inStock\",\"type\":\"boolean\",\"default\":false},{\"name\":\"id\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"category\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"fp\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"option\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"ts\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<EventData> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<EventData> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<EventData> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<EventData> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<EventData> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this EventData to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a EventData from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a EventData instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static EventData fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private long eid;
  private long quantity;
  private java.lang.String name;
  private long costPrice;
  private boolean onSale;
  private boolean inStock;
  private java.lang.String id;
  private java.lang.String category;
  private java.lang.String fp;
  private java.lang.String option;
  private long ts;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public EventData() {}

  /**
   * All-args constructor.
   * @param eid The new value for eid
   * @param quantity The new value for quantity
   * @param name The new value for name
   * @param costPrice The new value for costPrice
   * @param onSale The new value for onSale
   * @param inStock The new value for inStock
   * @param id The new value for id
   * @param category The new value for category
   * @param fp The new value for fp
   * @param option The new value for option
   * @param ts The new value for ts
   */
  public EventData(java.lang.Long eid, java.lang.Long quantity, java.lang.String name, java.lang.Long costPrice, java.lang.Boolean onSale, java.lang.Boolean inStock, java.lang.String id, java.lang.String category, java.lang.String fp, java.lang.String option, java.lang.Long ts) {
    this.eid = eid;
    this.quantity = quantity;
    this.name = name;
    this.costPrice = costPrice;
    this.onSale = onSale;
    this.inStock = inStock;
    this.id = id;
    this.category = category;
    this.fp = fp;
    this.option = option;
    this.ts = ts;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return eid;
    case 1: return quantity;
    case 2: return name;
    case 3: return costPrice;
    case 4: return onSale;
    case 5: return inStock;
    case 6: return id;
    case 7: return category;
    case 8: return fp;
    case 9: return option;
    case 10: return ts;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: eid = (java.lang.Long)value$; break;
    case 1: quantity = (java.lang.Long)value$; break;
    case 2: name = value$ != null ? value$.toString() : null; break;
    case 3: costPrice = (java.lang.Long)value$; break;
    case 4: onSale = (java.lang.Boolean)value$; break;
    case 5: inStock = (java.lang.Boolean)value$; break;
    case 6: id = value$ != null ? value$.toString() : null; break;
    case 7: category = value$ != null ? value$.toString() : null; break;
    case 8: fp = value$ != null ? value$.toString() : null; break;
    case 9: option = value$ != null ? value$.toString() : null; break;
    case 10: ts = (java.lang.Long)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'eid' field.
   * @return The value of the 'eid' field.
   */
  public long getEid() {
    return eid;
  }


  /**
   * Sets the value of the 'eid' field.
   * @param value the value to set.
   */
  public void setEid(long value) {
    this.eid = value;
  }

  /**
   * Gets the value of the 'quantity' field.
   * @return The value of the 'quantity' field.
   */
  public long getQuantity() {
    return quantity;
  }


  /**
   * Sets the value of the 'quantity' field.
   * @param value the value to set.
   */
  public void setQuantity(long value) {
    this.quantity = value;
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.String getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'costPrice' field.
   * @return The value of the 'costPrice' field.
   */
  public long getCostPrice() {
    return costPrice;
  }


  /**
   * Sets the value of the 'costPrice' field.
   * @param value the value to set.
   */
  public void setCostPrice(long value) {
    this.costPrice = value;
  }

  /**
   * Gets the value of the 'onSale' field.
   * @return The value of the 'onSale' field.
   */
  public boolean getOnSale() {
    return onSale;
  }


  /**
   * Sets the value of the 'onSale' field.
   * @param value the value to set.
   */
  public void setOnSale(boolean value) {
    this.onSale = value;
  }

  /**
   * Gets the value of the 'inStock' field.
   * @return The value of the 'inStock' field.
   */
  public boolean getInStock() {
    return inStock;
  }


  /**
   * Sets the value of the 'inStock' field.
   * @param value the value to set.
   */
  public void setInStock(boolean value) {
    this.inStock = value;
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.lang.String getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.String value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'category' field.
   * @return The value of the 'category' field.
   */
  public java.lang.String getCategory() {
    return category;
  }


  /**
   * Sets the value of the 'category' field.
   * @param value the value to set.
   */
  public void setCategory(java.lang.String value) {
    this.category = value;
  }

  /**
   * Gets the value of the 'fp' field.
   * @return The value of the 'fp' field.
   */
  public java.lang.String getFp() {
    return fp;
  }


  /**
   * Sets the value of the 'fp' field.
   * @param value the value to set.
   */
  public void setFp(java.lang.String value) {
    this.fp = value;
  }

  /**
   * Gets the value of the 'option' field.
   * @return The value of the 'option' field.
   */
  public java.lang.String getOption() {
    return option;
  }


  /**
   * Sets the value of the 'option' field.
   * @param value the value to set.
   */
  public void setOption(java.lang.String value) {
    this.option = value;
  }

  /**
   * Gets the value of the 'ts' field.
   * @return The value of the 'ts' field.
   */
  public long getTs() {
    return ts;
  }


  /**
   * Sets the value of the 'ts' field.
   * @param value the value to set.
   */
  public void setTs(long value) {
    this.ts = value;
  }

  /**
   * Creates a new EventData RecordBuilder.
   * @return A new EventData RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventData.Builder newBuilder() {
    return new org.shaft.administration.kafka.avro.model.EventData.Builder();
  }

  /**
   * Creates a new EventData RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new EventData RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventData.Builder newBuilder(org.shaft.administration.kafka.avro.model.EventData.Builder other) {
    if (other == null) {
      return new org.shaft.administration.kafka.avro.model.EventData.Builder();
    } else {
      return new org.shaft.administration.kafka.avro.model.EventData.Builder(other);
    }
  }

  /**
   * Creates a new EventData RecordBuilder by copying an existing EventData instance.
   * @param other The existing instance to copy.
   * @return A new EventData RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventData.Builder newBuilder(org.shaft.administration.kafka.avro.model.EventData other) {
    if (other == null) {
      return new org.shaft.administration.kafka.avro.model.EventData.Builder();
    } else {
      return new org.shaft.administration.kafka.avro.model.EventData.Builder(other);
    }
  }

  /**
   * RecordBuilder for EventData instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<EventData>
    implements org.apache.avro.data.RecordBuilder<EventData> {

    private long eid;
    private long quantity;
    private java.lang.String name;
    private long costPrice;
    private boolean onSale;
    private boolean inStock;
    private java.lang.String id;
    private java.lang.String category;
    private java.lang.String fp;
    private java.lang.String option;
    private long ts;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(org.shaft.administration.kafka.avro.model.EventData.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.eid)) {
        this.eid = data().deepCopy(fields()[0].schema(), other.eid);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.quantity)) {
        this.quantity = data().deepCopy(fields()[1].schema(), other.quantity);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.costPrice)) {
        this.costPrice = data().deepCopy(fields()[3].schema(), other.costPrice);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.onSale)) {
        this.onSale = data().deepCopy(fields()[4].schema(), other.onSale);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.inStock)) {
        this.inStock = data().deepCopy(fields()[5].schema(), other.inStock);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.id)) {
        this.id = data().deepCopy(fields()[6].schema(), other.id);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.category)) {
        this.category = data().deepCopy(fields()[7].schema(), other.category);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.fp)) {
        this.fp = data().deepCopy(fields()[8].schema(), other.fp);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
      if (isValidValue(fields()[9], other.option)) {
        this.option = data().deepCopy(fields()[9].schema(), other.option);
        fieldSetFlags()[9] = other.fieldSetFlags()[9];
      }
      if (isValidValue(fields()[10], other.ts)) {
        this.ts = data().deepCopy(fields()[10].schema(), other.ts);
        fieldSetFlags()[10] = other.fieldSetFlags()[10];
      }
    }

    /**
     * Creates a Builder by copying an existing EventData instance
     * @param other The existing instance to copy.
     */
    private Builder(org.shaft.administration.kafka.avro.model.EventData other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.eid)) {
        this.eid = data().deepCopy(fields()[0].schema(), other.eid);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.quantity)) {
        this.quantity = data().deepCopy(fields()[1].schema(), other.quantity);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.name)) {
        this.name = data().deepCopy(fields()[2].schema(), other.name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.costPrice)) {
        this.costPrice = data().deepCopy(fields()[3].schema(), other.costPrice);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.onSale)) {
        this.onSale = data().deepCopy(fields()[4].schema(), other.onSale);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.inStock)) {
        this.inStock = data().deepCopy(fields()[5].schema(), other.inStock);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.id)) {
        this.id = data().deepCopy(fields()[6].schema(), other.id);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.category)) {
        this.category = data().deepCopy(fields()[7].schema(), other.category);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.fp)) {
        this.fp = data().deepCopy(fields()[8].schema(), other.fp);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.option)) {
        this.option = data().deepCopy(fields()[9].schema(), other.option);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.ts)) {
        this.ts = data().deepCopy(fields()[10].schema(), other.ts);
        fieldSetFlags()[10] = true;
      }
    }

    /**
      * Gets the value of the 'eid' field.
      * @return The value.
      */
    public long getEid() {
      return eid;
    }


    /**
      * Sets the value of the 'eid' field.
      * @param value The value of 'eid'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setEid(long value) {
      validate(fields()[0], value);
      this.eid = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'eid' field has been set.
      * @return True if the 'eid' field has been set, false otherwise.
      */
    public boolean hasEid() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'eid' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearEid() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'quantity' field.
      * @return The value.
      */
    public long getQuantity() {
      return quantity;
    }


    /**
      * Sets the value of the 'quantity' field.
      * @param value The value of 'quantity'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setQuantity(long value) {
      validate(fields()[1], value);
      this.quantity = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'quantity' field has been set.
      * @return True if the 'quantity' field has been set, false otherwise.
      */
    public boolean hasQuantity() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'quantity' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearQuantity() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.String getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setName(java.lang.String value) {
      validate(fields()[2], value);
      this.name = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearName() {
      name = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'costPrice' field.
      * @return The value.
      */
    public long getCostPrice() {
      return costPrice;
    }


    /**
      * Sets the value of the 'costPrice' field.
      * @param value The value of 'costPrice'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setCostPrice(long value) {
      validate(fields()[3], value);
      this.costPrice = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'costPrice' field has been set.
      * @return True if the 'costPrice' field has been set, false otherwise.
      */
    public boolean hasCostPrice() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'costPrice' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearCostPrice() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'onSale' field.
      * @return The value.
      */
    public boolean getOnSale() {
      return onSale;
    }


    /**
      * Sets the value of the 'onSale' field.
      * @param value The value of 'onSale'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setOnSale(boolean value) {
      validate(fields()[4], value);
      this.onSale = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'onSale' field has been set.
      * @return True if the 'onSale' field has been set, false otherwise.
      */
    public boolean hasOnSale() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'onSale' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearOnSale() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'inStock' field.
      * @return The value.
      */
    public boolean getInStock() {
      return inStock;
    }


    /**
      * Sets the value of the 'inStock' field.
      * @param value The value of 'inStock'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setInStock(boolean value) {
      validate(fields()[5], value);
      this.inStock = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'inStock' field has been set.
      * @return True if the 'inStock' field has been set, false otherwise.
      */
    public boolean hasInStock() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'inStock' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearInStock() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.lang.String getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setId(java.lang.String value) {
      validate(fields()[6], value);
      this.id = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearId() {
      id = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'category' field.
      * @return The value.
      */
    public java.lang.String getCategory() {
      return category;
    }


    /**
      * Sets the value of the 'category' field.
      * @param value The value of 'category'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setCategory(java.lang.String value) {
      validate(fields()[7], value);
      this.category = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'category' field has been set.
      * @return True if the 'category' field has been set, false otherwise.
      */
    public boolean hasCategory() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'category' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearCategory() {
      category = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'fp' field.
      * @return The value.
      */
    public java.lang.String getFp() {
      return fp;
    }


    /**
      * Sets the value of the 'fp' field.
      * @param value The value of 'fp'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setFp(java.lang.String value) {
      validate(fields()[8], value);
      this.fp = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'fp' field has been set.
      * @return True if the 'fp' field has been set, false otherwise.
      */
    public boolean hasFp() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'fp' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearFp() {
      fp = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'option' field.
      * @return The value.
      */
    public java.lang.String getOption() {
      return option;
    }


    /**
      * Sets the value of the 'option' field.
      * @param value The value of 'option'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setOption(java.lang.String value) {
      validate(fields()[9], value);
      this.option = value;
      fieldSetFlags()[9] = true;
      return this;
    }

    /**
      * Checks whether the 'option' field has been set.
      * @return True if the 'option' field has been set, false otherwise.
      */
    public boolean hasOption() {
      return fieldSetFlags()[9];
    }


    /**
      * Clears the value of the 'option' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearOption() {
      option = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    /**
      * Gets the value of the 'ts' field.
      * @return The value.
      */
    public long getTs() {
      return ts;
    }


    /**
      * Sets the value of the 'ts' field.
      * @param value The value of 'ts'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder setTs(long value) {
      validate(fields()[10], value);
      this.ts = value;
      fieldSetFlags()[10] = true;
      return this;
    }

    /**
      * Checks whether the 'ts' field has been set.
      * @return True if the 'ts' field has been set, false otherwise.
      */
    public boolean hasTs() {
      return fieldSetFlags()[10];
    }


    /**
      * Clears the value of the 'ts' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventData.Builder clearTs() {
      fieldSetFlags()[10] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventData build() {
      try {
        EventData record = new EventData();
        record.eid = fieldSetFlags()[0] ? this.eid : (java.lang.Long) defaultValue(fields()[0]);
        record.quantity = fieldSetFlags()[1] ? this.quantity : (java.lang.Long) defaultValue(fields()[1]);
        record.name = fieldSetFlags()[2] ? this.name : (java.lang.String) defaultValue(fields()[2]);
        record.costPrice = fieldSetFlags()[3] ? this.costPrice : (java.lang.Long) defaultValue(fields()[3]);
        record.onSale = fieldSetFlags()[4] ? this.onSale : (java.lang.Boolean) defaultValue(fields()[4]);
        record.inStock = fieldSetFlags()[5] ? this.inStock : (java.lang.Boolean) defaultValue(fields()[5]);
        record.id = fieldSetFlags()[6] ? this.id : (java.lang.String) defaultValue(fields()[6]);
        record.category = fieldSetFlags()[7] ? this.category : (java.lang.String) defaultValue(fields()[7]);
        record.fp = fieldSetFlags()[8] ? this.fp : (java.lang.String) defaultValue(fields()[8]);
        record.option = fieldSetFlags()[9] ? this.option : (java.lang.String) defaultValue(fields()[9]);
        record.ts = fieldSetFlags()[10] ? this.ts : (java.lang.Long) defaultValue(fields()[10]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<EventData>
    WRITER$ = (org.apache.avro.io.DatumWriter<EventData>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<EventData>
    READER$ = (org.apache.avro.io.DatumReader<EventData>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeLong(this.eid);

    out.writeLong(this.quantity);

    if (this.name == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.name);
    }

    out.writeLong(this.costPrice);

    out.writeBoolean(this.onSale);

    out.writeBoolean(this.inStock);

    if (this.id == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.id);
    }

    if (this.category == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.category);
    }

    out.writeString(this.fp);

    if (this.option == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.option);
    }

    out.writeLong(this.ts);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.eid = in.readLong();

      this.quantity = in.readLong();

      if (in.readIndex() != 1) {
        in.readNull();
        this.name = null;
      } else {
        this.name = in.readString();
      }

      this.costPrice = in.readLong();

      this.onSale = in.readBoolean();

      this.inStock = in.readBoolean();

      if (in.readIndex() != 1) {
        in.readNull();
        this.id = null;
      } else {
        this.id = in.readString();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.category = null;
      } else {
        this.category = in.readString();
      }

      this.fp = in.readString();

      if (in.readIndex() != 1) {
        in.readNull();
        this.option = null;
      } else {
        this.option = in.readString();
      }

      this.ts = in.readLong();

    } else {
      for (int i = 0; i < 11; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.eid = in.readLong();
          break;

        case 1:
          this.quantity = in.readLong();
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.name = null;
          } else {
            this.name = in.readString();
          }
          break;

        case 3:
          this.costPrice = in.readLong();
          break;

        case 4:
          this.onSale = in.readBoolean();
          break;

        case 5:
          this.inStock = in.readBoolean();
          break;

        case 6:
          if (in.readIndex() != 1) {
            in.readNull();
            this.id = null;
          } else {
            this.id = in.readString();
          }
          break;

        case 7:
          if (in.readIndex() != 1) {
            in.readNull();
            this.category = null;
          } else {
            this.category = in.readString();
          }
          break;

        case 8:
          this.fp = in.readString();
          break;

        case 9:
          if (in.readIndex() != 1) {
            in.readNull();
            this.option = null;
          } else {
            this.option = in.readString();
          }
          break;

        case 10:
          this.ts = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










