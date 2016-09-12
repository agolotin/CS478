package decision_tree_learning;

// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Exception;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Matrix {
	// Data
	ArrayList< double[] > m_data;

	// Meta-data
	ArrayList< String > m_attr_name;
	ArrayList< TreeMap<String, Integer> > m_str_to_enum;
	ArrayList< TreeMap<Integer, String> > m_enum_to_str;

	static double MISSING = Double.MAX_VALUE; // representation of missing values in the dataset
	
	public boolean missing_val = false;
	
	public boolean hasMissing() {
		return missing_val;
	}

	// Creates a 0x0 matrix. You should call loadARFF or setSize next.
	public Matrix() {}
	
	public Matrix(Matrix that) {
		this.m_attr_name = new ArrayList<String>(that.m_attr_name);
		this.m_enum_to_str = new ArrayList<TreeMap<Integer, String>>(that.m_enum_to_str);
		this.m_str_to_enum = new ArrayList<TreeMap<String, Integer>>(that.m_str_to_enum);
	}

	// Copies the specified portion of that matrix into this matrix
	public Matrix(Matrix that, int rowStart, int colStart, int rowCount, int colCount) {
		m_data = new ArrayList< double[] >();
		for(int j = 0; j < rowCount; j++) {
			double[] rowSrc = that.row(rowStart + j);
			double[] rowDest = new double[colCount];
			for(int i = 0; i < colCount; i++)
				rowDest[i] = rowSrc[colStart + i];
			m_data.add(rowDest);
		}
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		for(int i = 0; i < colCount; i++) {
			m_attr_name.add(that.attrName(colStart + i));
			m_str_to_enum.add(that.m_str_to_enum.get(colStart + i));
			m_enum_to_str.add(that.m_enum_to_str.get(colStart + i));
		}
	}

	// Adds a copy of the specified portion of that matrix to this matrix
	public void add(Matrix that, int rowStart, int colStart, int rowCount) throws Exception {
		if(colStart + cols() > that.cols())
			throw new Exception("out of range");
		for(int i = 0; i < cols(); i++) {
			if(that.valueCount(colStart + i) != valueCount(i))
				throw new Exception("incompatible relations");
		}
		for(int j = 0; j < rowCount; j++) {
			double[] rowSrc = that.row(rowStart + j);
			double[] rowDest = new double[cols()];
			for(int i = 0; i < cols(); i++)
				rowDest[i] = rowSrc[colStart + i];
			m_data.add(rowDest);
		}
	}

	// Resizes this matrix (and sets all attributes to be continuous)
	public void setSize(int rows, int cols) {
		m_data = new ArrayList< double[] >();
		for(int j = 0; j < rows; j++) {
			double[] row = new double[cols];
			m_data.add(row);
		}
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		for(int i = 0; i < cols; i++) {
			m_attr_name.add("");
			m_str_to_enum.add(new TreeMap<String, Integer>());
			m_enum_to_str.add(new TreeMap<Integer, String>());
		}
	}

	// Loads from an ARFF file
	public void loadArff(String filename) throws Exception, FileNotFoundException {
		m_data = new ArrayList<double[]>();
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		boolean READDATA = false;
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
			String line = s.nextLine().trim();
			if (line.length() > 0 && line.charAt(0) != '%') {
				if (!READDATA) {
					
					Scanner t = new Scanner(line);
					String firstToken = t.next().toUpperCase();
					
					if (firstToken.equals("@RELATION")) {
						String datasetName = t.nextLine();
					}
					
					if (firstToken.equals("@ATTRIBUTE")) {
						TreeMap<String, Integer> ste = new TreeMap<String, Integer>();
						m_str_to_enum.add(ste);
						TreeMap<Integer, String> ets = new TreeMap<Integer, String>();
						m_enum_to_str.add(ets);

						Scanner u = new Scanner(line);
						if (line.indexOf("'") != -1) u.useDelimiter("'");
						u.next();
						String attributeName = u.next();
						if (line.indexOf("'") != -1) attributeName = "'" + attributeName + "'";
						m_attr_name.add(attributeName);

						int vals = 0;
						String type = u.next().trim().toUpperCase();
						if (type.equals("REAL") || type.equals("CONTINUOUS") || type.equals("INTEGER") || type.equals("NUMERIC")) {
						}
						else {
							try {
								String values = line.substring(line.indexOf("{")+1,line.indexOf("}"));
								Scanner v = new Scanner(values);
								v.useDelimiter(",");
								while (v.hasNext()) {
									String value = v.next().trim();
									if(value.length() > 0)
									{
										ste.put(value, new Integer(vals));
										ets.put(new Integer(vals), value);
										vals++;
									}
								}
							}
							catch (Exception e) {
								throw new Exception("Error parsing line: " + line + "\n" + e.toString());
							}
						}
					}
					if (firstToken.equals("@DATA")) {
						READDATA = true;
					}
				}
				else {
					double[] newrow = new double[cols()];
					int curPos = 0;

					try {
						Scanner t = new Scanner(line);
						t.useDelimiter(",");
						while (t.hasNext()) {
							String textValue = t.next().trim();
							//System.out.println(textValue);

							if (textValue.length() > 0) {
								double doubleValue;
								int vals = m_enum_to_str.get(curPos).size();
								
								//Missing instances appear in the dataset as a double defined as MISSING
								if (textValue.equals("?")) {
									missing_val = true;
									
									doubleValue = MISSING;
								}
								// Continuous values appear in the instance vector as they are
								else if (vals == 0) {
									doubleValue = Double.parseDouble(textValue);
								}
								// Discrete values appear as an index to the "name" 
								// of that value in the "attributeValue" structure
								else {
									doubleValue = m_str_to_enum.get(curPos).get(textValue);
									if (doubleValue == -1) {
										throw new Exception("Error parsing the value '" + textValue + "' on line: " + line);
									}
								}
								
								newrow[curPos] = doubleValue;
								curPos++;
							}
						}
					}
					catch(Exception e) {
						throw new Exception("Error parsing line: " + line + "\n" + e.toString());
					}
					m_data.add(newrow);
				}
			}
		}
		if (hasMissing())
			postmodifyMetadata();
	}
	
	public void postmodifyMetadata() {
		Iterator<TreeMap<Integer, String>> s_m_enum_to_str = this.m_enum_to_str.iterator();
        for (int i = 0; i < this.m_str_to_enum.size()-1; i++) { // we don't wanna touch last attribute's metadata
        	TreeMap<String, Integer> s_m_str_to_enum = this.m_str_to_enum.get(i);
			int max = Collections.max(s_m_str_to_enum.values());
			s_m_str_to_enum.put("MISSING", max+1);
			s_m_enum_to_str.next().put(max+1, "MISSING");
		}
	}

	// Returns the number of rows in the matrix
	int rows() { return m_data.size(); }

	// Returns the number of columns (or attributes) in the matrix
	int cols() { return m_attr_name.size(); }

	// Returns the specified row
	double[] row(int r) { return m_data.get(r); }

	// Returns the element at the specified row and column
	double get(int r, int c) { return m_data.get(r)[c]; }

	// Sets the value at the specified row and column
	void set(int r, int c, double v) { row(r)[c] = v; }

	// Returns the name of the specified attribute
	String attrName(int col) { return m_attr_name.get(col); }

	// Set the name of the specified attribute
	void setAttrName(int col, String name) { m_attr_name.set(col, name); }

	// Returns the name of the specified value
	String attrValue(int attr, int val) { return m_enum_to_str.get(attr).get(val); }

	// Returns the number of values associated with the specified attribute (or column)
	// 0=continuous, 2=binary, 3=trinary, etc.
	int valueCount(int col) { return m_enum_to_str.get(col).size(); }
	
	public Map<Range, Double> getContinuousValueCounts(double col, double col2) {
		Map<Range, Double> values = new HashMap<Range, Double>();
		double min1 = 100;
		double max1 = 0;
		double min2 = 100;
		double max2 = 0;
		
		for (double[] single_m_data : m_data) {
			double val1 = single_m_data[(int)col];
			double val2 = single_m_data[(int)col2];
			if (val1 > max1)
				max1 = val1;
			else if (val1 < min1)
				min1 = val1;
			
			if (val2 > max2)
				max2 = val2;
			else if (val2 < min2)
				min2 = val2;
		}
		double step1 = (max1 - min1) / 10.0;
		double step2 = (max2 - min2) / 10.0;
		
		double next1 = 0;
		double next2 = 0;
		for (int i = 0; i < 10; i++) {
			if (i == 0) {
				double t_next1 = secondDecimal(min1+step1);
				double t_next2 = secondDecimal(min2+step2);
				
				Range1 r1 = new Range1(min1, t_next1);
				Range1 r2 = new Range1(min2, t_next2);
				values.put(new Range(r1, r2), 0.0);
//				values.put(new Range(min, t_next), 0.0);
				
				next1 = t_next1 + 0.1;
				next2 = t_next2 + 0.1;
			}
			else if (i == 9) {
				Range1 r1 = new Range1(secondDecimal(next1), max1);
				Range1 r2 = new Range1(secondDecimal(next2), max2);
				values.put(new Range(r1, r2), 0.0);
//				values.put(new Range(secondDecimal(next), max), 0.0);
			}
			else {
				double t_next1 = secondDecimal(next1+step1);
				double t_next2 = secondDecimal(next2+step2);
				
				Range1 r1 = new Range1(secondDecimal(next1), t_next1);
				Range1 r2 = new Range1(secondDecimal(next2), t_next2);
				values.put(new Range(r1, r2), 0.0);
//				values.put(new Range(secondDecimal(next), t_next), 0.0);
				
				next1 = t_next1 + 0.1;
				next2 = t_next2 + 0.1;
			}
		}
		
		for (double[] single_m_data : m_data) {
			double val1 = single_m_data[(int)col];
			double val2 = single_m_data[(int)col2];
			for (Entry<Range, Double> s_val : values.entrySet())
			{
				if (s_val.getKey().getVal1().within(val1) && s_val.getKey().getVal2().within(val2)) {
					values.put(s_val.getKey(), 1.0 + s_val.getValue());
					break;
				}
			}
		}
		
		return values;
	}
	
	private double secondDecimal(double input) {
		DecimalFormat df = new DecimalFormat("#.#");
		String out = df.format(input);
		return Double.parseDouble(out);
	}
	
	public Map<Double, Double> getNominalValueCounts(double col) {
		Map<Double, Double> values = new HashMap<Double, Double>();
		for (double[] single_m_data : m_data) 
		{
			double val = single_m_data[((int)col)];
			if (!values.containsKey(val))
				values.put(val, 1.0);
			else 
				values.put(val, 1.0 + values.get(val));
		}
		
		return values;
	}
	
	//gets row indices
	public ArrayList<Integer> getRowsWithAttrValues(double attr, double attr2, Range value) throws Exception {
		int r = rows();
		if (attr >= cols()) 
			new Exception("getRowsWithAttrValues::Invalid attr number");
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < r; i++) 
		{
			double val1 = row(i)[(int)attr];
			double val2 = row(i)[(int)attr2];
			if (value.getVal1().within(val1) && value.getVal2().within(val2))//&& value.getVal2().within(row(i)[(int)attr2]))
				result.add(i);
		}
		
		return result;
	}
	
	private void premodifyReducedMatrix(Matrix reduced) 
	{
		reduced.setSize(0, this.cols());
		
		reduced.m_attr_name = new ArrayList<String>(this.m_attr_name);
		reduced.m_enum_to_str = new ArrayList<TreeMap<Integer, String>>(this.m_enum_to_str);
		reduced.m_str_to_enum = new ArrayList<TreeMap<String, Integer>>(this.m_str_to_enum);
	}
	
	public ArrayList<Integer> reduce(Matrix reduced, double attr, double attr2, Range value) throws Exception {
		premodifyReducedMatrix(reduced);
		double max = (attr > attr2 ? attr : attr2);
		double min = (attr < attr2 ? attr : attr2);
		//Remove target attribute
		reduced.m_attr_name.remove((int)max);
		reduced.m_enum_to_str.remove((int)max);
		reduced.m_str_to_enum.remove((int)max);
		reduced.m_attr_name.remove((int)min);
		reduced.m_enum_to_str.remove((int)min);
		reduced.m_str_to_enum.remove((int)min);
		
		ArrayList<Integer> keepRows = getRowsWithAttrValues(attr, attr2, value);
		for (int i = 0; i < keepRows.size(); i++) {
			double[] row = this.row(keepRows.get(i));
			row = ArrayUtils.remove(row, (int)max);
			row = ArrayUtils.remove(row, (int)min);
			
			reduced.m_data.add(row);
		}
		return keepRows;
	}
	
	public void reduce(Matrix reduced, ArrayList<Integer> rows) {
		premodifyReducedMatrix(reduced);
		
		for (int i = 0; i < rows.size(); i++) {
			double[] row = this.row(rows.get(i));
			reduced.m_data.add(row);
		}
	}
	
	public ArrayList<Range> allAttrValues(Range1 attr) {
//		TreeMap<String, Integer> attrMap = this.m_str_to_enum.get((int)attr);
//		ArrayList<Integer> attrValues = new ArrayList<Integer>();
//        for (Entry<String, Integer> s_attrMap : attrMap.entrySet()) 
//        	attrValues.add(s_attrMap.getValue());
//        	
//		return attrValues;
		String see = "I'm seeing";
		return null;
	}
	
	// Shuffles the row order
	void shuffle(Random rand) {
		for(int n = rows(); n > 0; n--) {
			int i = rand.nextInt(n);
			double[] tmp = row(n - 1);
			m_data.set(n - 1, row(i));
			m_data.set(i, tmp);
		}
	}

	// Shuffles the row order with a buddy matrix 
	void shuffle(Random rand, Matrix buddy) {
		for (int n = rows(); n > 0; n--) {
			int i = rand.nextInt(n);
			double[] tmp = row(n - 1);
			m_data.set(n - 1, row(i));
			m_data.set(i, tmp);


			double[] tmp1 = buddy.row(n - 1);
			buddy.m_data.set(n - 1, buddy.row(i));
			buddy.m_data.set(i, tmp1);
		}
	}

	// Returns the mean of the specified column
	double columnMean(int col) {
		double sum = 0;
		int count = 0;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				sum += v;
				count++;
			}
		}
		return sum / count;
	}

	// Returns the min value in the specified column
	double columnMin(int col) {
		double m = MISSING;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				if(m == MISSING || v < m)
					m = v;
			}
		}
		return m;
	}

	// Returns the max value in the specified column
	double columnMax(int col) {
		double m = MISSING;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				if(m == MISSING || v > m)
					m = v;
			}
		}
		return m;
	}

	// Returns the most common value in the specified column
	double mostCommonValue(int col) {
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				Integer count = tm.get(v);
				if(count == null)
					tm.put(v, new Integer(1));
				else
					tm.put(v, new Integer(count.intValue() + 1));
			}
		}
		int maxCount = 0;
		double val = MISSING;
		Iterator< Entry<Double, Integer> > it = tm.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Double, Integer> e = it.next();
			if(e.getValue() > maxCount)
			{
				maxCount = e.getValue();
				val = e.getKey();
			}
		}
		return val;
	}

	void normalize() {
		for(int i = 0; i < cols(); i++) {
			if(valueCount(i) == 0) {
				double min = columnMin(i);
				double max = columnMax(i);
				for(int j = 0; j < rows(); j++) {
					double v = get(j, i);
					if(v != MISSING)
						set(j, i, (v - min) / (max - min));
				}
			}
		}
	}

	void print() {
		System.out.println("@RELATION Untitled");
		for(int i = 0; i < m_attr_name.size(); i++) {
			System.out.print("@ATTRIBUTE " + m_attr_name.get(i));
			int vals = valueCount(i);
			if(vals == 0)
				System.out.println(" CONTINUOUS");
			else
			{
				System.out.print(" {");
				for(int j = 0; j < vals; j++) {
					if(j > 0)
						System.out.print(", ");
					System.out.print(m_enum_to_str.get(i).get(j));
				}
				System.out.println("}");
			}
		}
		System.out.println("@DATA");
		for(int i = 0; i < rows(); i++) {
			double[] r = row(i);
			for(int j = 0; j < r.length; j++) {
				if(j > 0)
					System.out.print(", ");
				if(valueCount(j) == 0)
					System.out.print(r[j]);
				else
					System.out.print(m_enum_to_str.get(j).get((int)r[j]));
			}
			System.out.println("");
		}
	}
}
