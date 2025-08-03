const config = {
  useMock: import.meta.env.VITE_USE_MOCK === 'true',
  bankA: {
    name: 'Bank A' as const,
    bic: 'TESTGETBXXX',
    client: {
      name: 'Marcus Vellon',
      id: '1'
    }
  },
  bankB: {
    name: 'Bank B' as const,
    bic: 'TESTUKLLXXX',
    client: {
      name: 'Lena Brightfield',
      id: '2'
    }
  }
}

export default config

export type BankName = typeof config.bankA.name | typeof config.bankB.name
