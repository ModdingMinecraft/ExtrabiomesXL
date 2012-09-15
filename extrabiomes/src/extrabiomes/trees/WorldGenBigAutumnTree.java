/**
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license
 * located in /MMPL-1.0.txt
 */

package extrabiomes.trees;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

public class WorldGenBigAutumnTree extends WorldGenerator {
	private static final byte[]						otherCoordPairs		= new byte[] {
			(byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1	};
	private final Random							rand				= new Random();
	private World									world;
	private final int[]								basePos				= new int[] {
			0, 0, 0													};
	private int										heightLimit			= 0;
	private int										height;
	private final double							heightAttenuation	= 0.618D;
	private final double							branchSlope			= 0.381D;
	private double									scaleWidth			= 1.0D;
	private double									leafDensity			= 1.0D;
	private int										heightLimitLimit	= 12;
	private int										leafDistanceLimit	= 4;
	private int[][]									leafNodes;
	private final WorldGenAutumnTree.AutumnTreeType	type;

	public WorldGenBigAutumnTree(boolean notify,
			WorldGenAutumnTree.AutumnTreeType type)
	{
		super(notify);
		this.type = type;
	}

	/**
	 * Checks a line of blocks in the world from the first coordinate to
	 * triplet to the second, returning the distance (in blocks) before
	 * a non-air, non-leaf block is encountered and/or the end is
	 * encountered.
	 */
	int checkBlockLine(int[] par1ArrayOfInteger,
			int[] par2ArrayOfInteger)
	{
		final int[] var3 = new int[] { 0, 0, 0 };
		byte var4 = 0;
		byte var5;

		for (var5 = 0; var4 < 3; ++var4) {
			var3[var4] = par2ArrayOfInteger[var4]
					- par1ArrayOfInteger[var4];

			if (Math.abs(var3[var4]) > Math.abs(var3[var5]))
				var5 = var4;
		}

		if (var3[var5] == 0)
			return -1;
		else {
			final byte var6 = otherCoordPairs[var5];
			final byte var7 = otherCoordPairs[var5 + 3];
			byte var8;

			if (var3[var5] > 0)
				var8 = 1;
			else
				var8 = -1;

			final double var9 = (double) var3[var6]
					/ (double) var3[var5];
			final double var11 = (double) var3[var7]
					/ (double) var3[var5];
			final int[] coord = new int[] { 0, 0, 0 };
			int var14 = 0;
			int var15;

			for (var15 = var3[var5] + var8; var14 != var15; var14 += var8)
			{
				coord[var5] = par1ArrayOfInteger[var5] + var14;
				coord[var6] = MathHelper
						.floor_double(par1ArrayOfInteger[var6] + var14
								* var9);
				coord[var7] = MathHelper
						.floor_double(par1ArrayOfInteger[var7] + var14
								* var11);
				final int id = world.getBlockId(coord[0], coord[1],
						coord[2]);

				if (id != 0
						&& !Block.blocksList[id].isLeaves(world,
								coord[0], coord[1], coord[2])) break;
			}

			return var14 == var15 ? -1 : Math.abs(var14);
		}
	}

	@Override
	public boolean generate(World par1World, Random par2Random,
			int par3, int par4, int par5)
	{
		TreeBlocks.Type treeType = null;

		switch (type) {
			case BROWN:
				treeType = TreeBlocks.Type.BROWN;
				break;
			case ORANGE:
				treeType = TreeBlocks.Type.ORANGE;
				break;
			case PURPLE:
				treeType = TreeBlocks.Type.PURPLE;
				break;
			case YELLOW:
				treeType = TreeBlocks.Type.YELLOW;
		}

		final int leafID = TreeBlocks.getLeafID(treeType);
		final int leafMeta = TreeBlocks.getLeafMeta(treeType);
		final int woodID = TreeBlocks.getWoodID(treeType);
		final int woodMeta = TreeBlocks.getWoodMeta(treeType);

		world = par1World;
		final long var6 = par2Random.nextLong();
		rand.setSeed(var6);
		basePos[0] = par3;
		basePos[1] = par4;
		basePos[2] = par5;

		if (heightLimit == 0)
			heightLimit = 5 + rand.nextInt(heightLimitLimit);

		if (!validTreeLocation()) return false;

		generateLeafNodeList();
		generateLeaves(leafID, leafMeta);
		generateTrunk(woodID, woodMeta);
		generateLeafNodeBases(woodID, woodMeta);
		return true;
	}

	/**
	 * Generates the leaves surrounding an individual entry in the
	 * leafNodes list.
	 */
	void generateLeafNode(int x, int y, int z, int leafID, int leafMeta)
	{
		int y1 = y;

		for (final int heightLimit = y + leafDistanceLimit; y1 < heightLimit; ++y1)
		{
			final float size = leafSize(y1 - y);
			genTreeLayer(x, y1, z, size, (byte) 1, leafID, leafMeta);
		}
	}

	/**
	 * Generates additional wood blocks to fill out the bases of
	 * different leaf nodes that would otherwise degrade.
	 */
	void generateLeafNodeBases(int woodID, int woodMeta) {
		int var1 = 0;
		final int var2 = leafNodes.length;

		for (final int[] var3 = new int[] { basePos[0], basePos[1],
				basePos[2] }; var1 < var2; ++var1)
		{
			final int[] var4 = leafNodes[var1];
			final int[] var5 = new int[] { var4[0], var4[1], var4[2] };
			var3[1] = var4[3];
			final int var6 = var3[1] - basePos[1];

			if (leafNodeNeedsBase(var6))
				placeBlockLine(var3, var5, woodID, woodMeta);
		}
	}

	void generateLeafNodeList() {
		height = (int) (heightLimit * heightAttenuation);

		if (height >= heightLimit) height = heightLimit - 1;

		int var1 = (int) (1.382D + Math.pow(leafDensity * heightLimit
				/ 13.0D, 2.0D));

		if (var1 < 1) var1 = 1;

		final int[][] var2 = new int[var1 * heightLimit][4];
		int var3 = basePos[1] + heightLimit - leafDistanceLimit;
		int var4 = 1;
		final int var5 = basePos[1] + height;
		int var6 = var3 - basePos[1];
		var2[0][0] = basePos[0];
		var2[0][1] = var3;
		var2[0][2] = basePos[2];
		var2[0][3] = var5;
		--var3;

		while (var6 >= 0) {
			int var7 = 0;
			final float var8 = layerSize(var6);

			if (var8 < 0.0F) {
				--var3;
				--var6;
			} else {
				for (final double var9 = 0.5D; var7 < var1; ++var7) {
					final double var11 = scaleWidth * var8
							* (rand.nextFloat() + 0.328D);
					final double var13 = rand.nextFloat() * 2.0D
							* Math.PI;
					final int var15 = MathHelper.floor_double(var11
							* Math.sin(var13) + basePos[0] + var9);
					final int var16 = MathHelper.floor_double(var11
							* Math.cos(var13) + basePos[2] + var9);
					final int[] var17 = new int[] { var15, var3, var16 };
					final int[] var18 = new int[] { var15,
							var3 + leafDistanceLimit, var16 };

					if (checkBlockLine(var17, var18) == -1) {
						final int[] var19 = new int[] { basePos[0],
								basePos[1], basePos[2] };
						final double var20 = Math
								.sqrt(Math.pow(
										Math.abs(basePos[0] - var17[0]),
										2.0D)
										+ Math.pow(
												Math.abs(basePos[2]
														- var17[2]),
												2.0D));
						final double var22 = var20 * branchSlope;

						if (var17[1] - var22 > var5)
							var19[1] = var5;
						else
							var19[1] = (int) (var17[1] - var22);

						if (checkBlockLine(var19, var17) == -1) {
							var2[var4][0] = var15;
							var2[var4][1] = var3;
							var2[var4][2] = var16;
							var2[var4][3] = var19[1];
							++var4;
						}
					}
				}

				--var3;
				--var6;
			}
		}

		leafNodes = new int[var4][4];
		System.arraycopy(var2, 0, leafNodes, 0, var4);
	}

	/**
	 * Generates the leaf portion of the tree as specified by the
	 * leafNodes list.
	 */
	void generateLeaves(int leafID, int leafMeta) {
		int node = 0;

		for (final int length = leafNodes.length; node < length; ++node)
			generateLeafNode(leafNodes[node][0], leafNodes[node][1],
					leafNodes[node][2], leafID, leafMeta);
	}

	/**
	 * Places the trunk for the big tree that is being generated. Able
	 * to generate double-sized trunks by changing a field that is
	 * always 1 to 2.
	 */
	void generateTrunk(int woodID, int woodMeta) {
		final int var1 = basePos[0];
		final int var2 = basePos[1];
		final int var3 = basePos[1] + height;
		final int var4 = basePos[2];
		final int[] var5 = new int[] { var1, var2, var4 };
		final int[] var6 = new int[] { var1, var3, var4 };
		placeBlockLine(var5, var6, woodID, woodMeta);
	}

	void genTreeLayer(int x, int y, int z, float size, byte par5,
			int leafBlockID, int leafBlockMeta)
	{
		final int var7 = (int) (size + 0.618D);
		final byte var8 = otherCoordPairs[par5];
		final byte var9 = otherCoordPairs[par5 + 3];
		final int[] var10 = new int[] { x, y, z };
		final int[] var11 = new int[] { 0, 0, 0 };
		int var12 = -var7;
		int var13 = -var7;

		for (var11[par5] = var10[par5]; var12 <= var7; ++var12) {
			var11[var8] = var10[var8] + var12;
			var13 = -var7;

			while (var13 <= var7) {
				final double var15 = Math.pow(Math.abs(var12) + 0.5D,
						2.0D) + Math.pow(Math.abs(var13) + 0.5D, 2.0D);

				if (var15 > size * size)
					++var13;
				else {
					var11[var9] = var10[var9] + var13;
					final int blockID = world.getBlockId(var11[0],
							var11[1], var11[2]);

					if (blockID != 0
							&& !Block.blocksList[blockID]
									.isLeaves(world, var11[0],
											var11[1], var11[2]))
						++var13;
					else {
						setBlockAndMetadata(world, var11[0], var11[1],
								var11[2], leafBlockID, leafBlockMeta);
						++var13;
					}
				}
			}
		}
	}

	/**
	 * Gets the rough size of a layer of the tree.
	 */
	float layerSize(int par1) {
		if (par1 < heightLimit * 0.3D)
			return -1.618F;
		else {
			final float var2 = heightLimit / 2.0F;
			final float var3 = heightLimit / 2.0F - par1;
			float var4;

			if (var3 == 0.0F)
				var4 = var2;
			else if (Math.abs(var3) >= var2)
				var4 = 0.0F;
			else
				var4 = (float) Math.sqrt(Math.pow(Math.abs(var2), 2.0D)
						- Math.pow(Math.abs(var3), 2.0D));

			var4 *= 0.5F;
			return var4;
		}
	}

	/**
	 * Indicates whether or not a leaf node requires additional wood to
	 * be added to preserve integrity.
	 */
	boolean leafNodeNeedsBase(int par1) {
		return par1 >= heightLimit * 0.2D;
	}

	float leafSize(int par1) {
		return par1 >= 0 && par1 < leafDistanceLimit ? par1 != 0
				&& par1 != leafDistanceLimit - 1 ? 3.0F : 2.0F : -1.0F;
	}

	/**
	 * Places a line of the specified block ID into the world from the
	 * first coordinate triplet to the second.
	 */
	void placeBlockLine(int[] par1ArrayOfInteger,
			int[] par2ArrayOfInteger, int woodID, int woodMeta)
	{
		final int[] var4 = new int[] { 0, 0, 0 };
		byte var5 = 0;
		byte var6;

		for (var6 = 0; var5 < 3; ++var5) {
			var4[var5] = par2ArrayOfInteger[var5]
					- par1ArrayOfInteger[var5];

			if (Math.abs(var4[var5]) > Math.abs(var4[var6]))
				var6 = var5;
		}

		if (var4[var6] != 0) {
			final byte var7 = otherCoordPairs[var6];
			final byte var8 = otherCoordPairs[var6 + 3];
			byte var9;

			if (var4[var6] > 0)
				var9 = 1;
			else
				var9 = -1;

			final double var10 = (double) var4[var7]
					/ (double) var4[var6];
			final double var12 = (double) var4[var8]
					/ (double) var4[var6];
			final int[] var14 = new int[] { 0, 0, 0 };
			int var15 = 0;

			for (final int var16 = var4[var6] + var9; var15 != var16; var15 += var9)
			{
				var14[var6] = MathHelper
						.floor_double(par1ArrayOfInteger[var6] + var15
								+ 0.5D);
				var14[var7] = MathHelper
						.floor_double(par1ArrayOfInteger[var7] + var15
								* var10 + 0.5D);
				var14[var8] = MathHelper
						.floor_double(par1ArrayOfInteger[var8] + var15
								* var12 + 0.5D);
				byte woodMetaWithDirection = (byte) woodMeta;
				final int var18 = Math.abs(var14[0]
						- par1ArrayOfInteger[0]);
				final int var19 = Math.abs(var14[2]
						- par1ArrayOfInteger[2]);
				final int var20 = Math.max(var18, var19);

				if (var20 > 0) if (var18 == var20)
					woodMetaWithDirection |= 4;
				else if (var19 == var20) woodMetaWithDirection |= 8;

				setBlockAndMetadata(world, var14[0], var14[1],
						var14[2], woodID, woodMetaWithDirection);
			}
		}
	}

	/**
	 * Rescales the generator settings, only used in WorldGenBigTree
	 */
	@Override
	public void setScale(double par1, double par3, double par5) {
		heightLimitLimit = (int) (par1 * 12.0D);

		if (par1 > 0.5D) leafDistanceLimit = 5;

		scaleWidth = par3;
		leafDensity = par5;
	}

	/**
	 * Returns a boolean indicating whether or not the current location
	 * for the tree, spanning basePos to to the height limit, is valid.
	 */
	boolean validTreeLocation() {
		final int[] var1 = new int[] { basePos[0], basePos[1],
				basePos[2] };
		final int[] var2 = new int[] { basePos[0],
				basePos[1] + heightLimit - 1, basePos[2] };
		final int var3 = world.getBlockId(basePos[0], basePos[1] - 1,
				basePos[2]);

		if (var3 != 2 && var3 != 3)
			return false;
		else {
			final int var4 = checkBlockLine(var1, var2);

			if (var4 == -1)
				return true;
			else if (var4 < 6)
				return false;
			else {
				heightLimit = var4;
				return true;
			}
		}
	}
}