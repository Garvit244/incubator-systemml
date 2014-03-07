/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2013
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.runtime.instructions.CPInstructions;

import com.ibm.bi.dml.runtime.DMLRuntimeException;
import com.ibm.bi.dml.runtime.DMLUnsupportedOperationException;
import com.ibm.bi.dml.runtime.controlprogram.ExecutionContext;
import com.ibm.bi.dml.runtime.matrix.io.MatrixBlock;
import com.ibm.bi.dml.runtime.matrix.operators.BinaryOperator;
import com.ibm.bi.dml.runtime.matrix.operators.Operator;


public class MatrixMatrixArithmeticCPInstruction extends ArithmeticBinaryCPInstruction
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2013\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	public MatrixMatrixArithmeticCPInstruction(Operator op, 
											   CPOperand in1, 
											   CPOperand in2, 
											   CPOperand out, 
											   String istr){
		super(op, in1, in2, out, istr);
	}
	
	@Override
	public void processInstruction(ExecutionContext ec) 
		throws DMLRuntimeException, DMLUnsupportedOperationException{

		long begin, st, tread, tcompute, twrite, ttotal;
		
		begin = System.currentTimeMillis();
		// Read input matrices
        MatrixBlock matBlock1 = ec.getMatrixInput(input1.get_name());
        MatrixBlock matBlock2 = ec.getMatrixInput(input2.get_name());
		tread = System.currentTimeMillis() - begin;
        
		st = System.currentTimeMillis();
		BinaryOperator bop = (BinaryOperator) optr;
		String output_name = output.get_name();
		// Perform computation using input matrices, and produce the result matrix
		MatrixBlock soresBlock = (MatrixBlock) (matBlock1.binaryOperations (bop, matBlock2, new MatrixBlock()));
		tcompute = System.currentTimeMillis() - st;
        
		st = System.currentTimeMillis();
		// Release the memory occupied by input matrices
		matBlock1 = matBlock2 = null;
		ec.releaseMatrixInput(input1.get_name());
		ec.releaseMatrixInput(input2.get_name());
		// Attach result matrix with MatrixObject associated with output_name
		ec.setMatrixOutput(output_name, soresBlock);
        soresBlock = null;
		
        twrite = System.currentTimeMillis()-st;
		ttotal = System.currentTimeMillis()-begin;
		
		LOG.trace("CPInst " + this.toString() + "\t" + tread + "\t" + tcompute + "\t" + twrite + "\t" + ttotal);
	}

}