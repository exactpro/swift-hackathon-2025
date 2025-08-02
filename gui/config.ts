const config = {
  useMock: import.meta.env.VITE_USE_MOCK === 'true',
  bankA: {
    name: 'Bank A' as const,
    bic: 'TESTGETBXXX',
    client: {
      name: 'Marcus Vellon',
      id: '226793bc-f5f4-4ce6-92fa-402ea8e486cc'
    }
  },
  bankB: {
    name: 'Bank B' as const,
    bic: 'TESTUKLLXXX',
    client: {
      name: 'Lena Brightfield',
      id: 'd4e5f6g7-h8i9-0j1k-2l3m-4n5o6p7q8r9s'
    }
  }
}

export default config

export type BankName = typeof config.bankA.name | typeof config.bankB.name
