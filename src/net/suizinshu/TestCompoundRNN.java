package net.suizinshu;

import dmonner.xlbp.Network;
import dmonner.xlbp.NetworkDotBuilder;
import dmonner.xlbp.UniformWeightInitializer;
import dmonner.xlbp.WeightUpdaterType;
import dmonner.xlbp.compound.InputCompound;
import dmonner.xlbp.compound.MemoryCellCompound;
import dmonner.xlbp.compound.XEntropyTargetCompound;

public class TestCompoundRNN {

	public static void main(String[] args) {
		
		/* DEFINE CONSTANTS */

		/**
		 * Ok, so this thing is REALLY important.
		 * Why the hell didn't donner do any better?
		 * I mean, seriously! JUST SERIOUSLY
		 * 
		 * It's just a DATA INPUT PARAMETER SERIES.
		 * 
		 * If mctype is an empty string, memory cells HAVE NO GATES
		 * 
		 * Now, there's weird claptrap about these letters.
		 * 
		 * I : Input
		 * F : Forget
		 * O : Output
		 * P : Peephole
		 * 
		 * Then more:
		 * 
		 * G : Lateral connections from all extant gate units to the memory cell (Gated by input gates)
		 * U : " ", Ungated
		 * 
		 * N : No memory
		 * T : Truncate errors at gates - behavior of the ORIGINAL training but this one might be smarter
		 * 
		 * ORIGINAL LSTM ARCHITECTURE THEREFORE WOULD BE "IOT"
		 * 
		 * No peepholes? "IFO"
		 * 
		 * Ungated lateral connections (apparently strong) "IFOU"
		 */
		final String mctype = "IFOP";
		
		// 5 inputs, 20 memcells, 2 outputs.
		final int inSize = 5;
		final int hidSize = 20;
		final int outSize = 2;
		
		/* CREATE COMPOUNDS */
		
		final InputCompound inComp = new InputCompound(mctype, inSize);
		// Name it hidden
		final MemoryCellCompound memCellComp = new MemoryCellCompound("Hidden", hidSize, mctype);
		// We can also add more
		final MemoryCellCompound memCellComp2 = new MemoryCellCompound("Hidden2", hidSize, mctype);
		// Indicates back-propogated cross-entropy error measure training method
		final XEntropyTargetCompound outComp = new XEntropyTargetCompound("Output", outSize);
		
		/* LINK WEIGHTS */
		
		// Connect things together.
		
		// Creates a weight matrix from memory cell outputs projecting to the output units and
		// automatically fills it with small random weight values.
		outComp.addUpstreamWeights(memCellComp2);
		
		// Same deal with mems and inputs
		// 4 matrices since IFO + cell = 4
		// We can also individually do stuff to single gate types
		// e.g. memCellComp.getForgetGates().addUpstreamWeights(inComp);
		memCellComp2.addUpstreamWeights(memCellComp);
		memCellComp.addUpstreamWeights(inComp);
		
		/* ACTUAL NETWORK? */
		
		// We have to collect info here
		// Also we have to name it
		final Network network = new Network("Canonical LSTM");
		
		// Set global properties
		
		// Type of weight updates - choose what is most appropriate
		network.setWeightUpdaterType(WeightUpdaterType.basic(0.1F));

		// Initialize weights (how weight matx are created)
		network.setWeightInitializer(new UniformWeightInitializer(1.0F, -0.1F, 0.1F));
		
		// Add our components
		network.add(inComp);
		network.add(memCellComp);
		network.add(memCellComp2);
		network.add(outComp);
		
		// Fix and build
		network.optimize();
		network.build();
		
		System.out.println(new NetworkDotBuilder(network));
		
		
		
	}
	
	
	
	
}
